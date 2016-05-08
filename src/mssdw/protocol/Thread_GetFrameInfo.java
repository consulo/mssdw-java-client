package mssdw.protocol;

import mssdw.JDWPException;
import mssdw.PacketStream;
import mssdw.ThreadMirror;
import mssdw.TypeRef;
import mssdw.VirtualMachineImpl;

/**
 * @author VISTALL
 * @since 09.04.14
 */
public class Thread_GetFrameInfo implements Thread
{
	static final int COMMAND = 1;

	public static Thread_GetFrameInfo process(VirtualMachineImpl vm, ThreadMirror thread) throws JDWPException
	{
		PacketStream ps = enqueueCommand(vm, thread);
		return waitForReply(vm, ps);
	}

	static PacketStream enqueueCommand(VirtualMachineImpl vm, ThreadMirror thread)
	{
		PacketStream ps = new PacketStream(vm, COMMAND_SET, COMMAND);
		ps.writeId(thread);
		ps.send();
		return ps;
	}

	static Thread_GetFrameInfo waitForReply(VirtualMachineImpl vm, PacketStream ps) throws JDWPException
	{
		ps.waitForReply();
		return new Thread_GetFrameInfo(vm, ps);
	}

	public static class Frame
	{
		public final int frameID;

		public String FilePath;

		public int Line;

		public int Column;

		public TypeRef TypeRef;

		public int FunctionToken;

		private Frame(VirtualMachineImpl vm, PacketStream ps)
		{
			frameID = ps.readInt();
			FilePath = ps.readString();
			Line = ps.readInt();
			Column = ps.readInt();
			TypeRef = ps.readTypeRef();
			FunctionToken = ps.readInt();
		}
	}

	public final Frame[] frames;

	private Thread_GetFrameInfo(VirtualMachineImpl vm, PacketStream ps)
	{
		int framesCount = ps.readInt();
		frames = new Frame[framesCount];
		for(int i = 0; i < framesCount; i++)
		{
			frames[i] = new Frame(vm, ps);
		}
	}
}
