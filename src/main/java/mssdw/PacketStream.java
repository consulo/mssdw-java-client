/*
 * Copyright (c) 1998, 2008, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package mssdw;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import consulo.internal.dotnet.asm.signature.SignatureConstants;

public class PacketStream
{
	private static final int VALUE_TYPE_ID_NULL = 0xf0;
	private static final int VALUE_TYPE_ID_TYPE = 0xf1;
	private static final int VALUE_TYPE_ID_PARENT_VTYPE = 0xf2;

	public final VirtualMachineImpl vm;
	private int inCursor = 0;
	public final Packet pkt;
	private ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
	private boolean isCommitted = false;

	public PacketStream(VirtualMachineImpl vm, int cmdSet, int cmd)
	{
		this.vm = vm;
		this.pkt = new Packet();
		pkt.cmdSet = (short) cmdSet;
		pkt.cmd = (short) cmd;
	}

	public PacketStream(VirtualMachineImpl vm, Packet pkt)
	{
		this.vm = vm;
		this.pkt = pkt;
		this.isCommitted = true; /* read only stream */
	}

	int id()
	{
		return pkt.id;
	}

	public void send()
	{
		if(!isCommitted)
		{
			pkt.data = dataStream.toByteArray();
			vm.sendToTarget(pkt);
			isCommitted = true;
		}
	}

	public void waitForReply() throws JDWPException
	{
		if(!isCommitted)
		{
			throw new InternalException("waitForReply without send");
		}

		vm.waitForTargetReply(pkt);

		if(pkt.errorCode != Packet.ReplyNoError)
		{
			throw new JDWPException(pkt.errorCode);
		}
	}

	public void writeByteBool(boolean data)
	{
		writeByte((byte) (data ? 1 : 0));
	}

	public void writeByte(byte data)
	{
		dataStream.write(data);
	}

	public void writeShort(short data)
	{
		dataStream.write((byte) ((data >>> 8) & 0xFF));
		dataStream.write((byte) ((data >>> 0) & 0xFF));
	}

	public void writeInt(int data)
	{
		dataStream.write((byte) ((data >>> 24) & 0xFF));
		dataStream.write((byte) ((data >>> 16) & 0xFF));
		dataStream.write((byte) ((data >>> 8) & 0xFF));
		dataStream.write((byte) ((data >>> 0) & 0xFF));
	}

	void writeLong(long data)
	{
		dataStream.write((byte) ((data >>> 56) & 0xFF));
		dataStream.write((byte) ((data >>> 48) & 0xFF));
		dataStream.write((byte) ((data >>> 40) & 0xFF));
		dataStream.write((byte) ((data >>> 32) & 0xFF));

		dataStream.write((byte) ((data >>> 24) & 0xFF));
		dataStream.write((byte) ((data >>> 16) & 0xFF));
		dataStream.write((byte) ((data >>> 8) & 0xFF));
		dataStream.write((byte) ((data >>> 0) & 0xFF));
	}

	void writeFloat(float data)
	{
		writeInt(Float.floatToIntBits(data));
	}

	void writeDouble(double data)
	{
		writeLong(Double.doubleToLongBits(data));
	}

	@Deprecated
	public void writeId(@Nullable MirrorWithId data)
	{
		throw new UnsupportedOperationException();
	}

	public void writeValue(Value<?> value)
	{
		if(value instanceof StringValueMirror)
		{
			writeByte(SignatureConstants.ELEMENT_TYPE_STRING);
			writeInt(((StringValueMirror) value).id());
		}
		else if(value instanceof CharValueMirror)
		{
			writeByte(SignatureConstants.ELEMENT_TYPE_CHAR);
			writeInt(((CharValueMirror) value).value());
		}
		else if(value instanceof BooleanValueMirror)
		{
			writeByte(SignatureConstants.ELEMENT_TYPE_BOOLEAN);
			writeByteBool(((BooleanValueMirror) value).value());
		}
		else if(value instanceof NumberValueMirror)
		{
			if(((NumberValueMirror) value).getTag() == SignatureConstants.ELEMENT_TYPE_PTR)
			{
				writeNumberValue(SignatureConstants.ELEMENT_TYPE_U8, ((NumberValueMirror) value).value());
			}
			else
			{
				writeNumberValue(((NumberValueMirror) value).getTag(), ((NumberValueMirror) value).value());
			}
		}
		else if(value instanceof ValueTypeValueMirror)
		{
			writeByte(SignatureConstants.ELEMENT_TYPE_VALUETYPE);
			ValueTypeValueMirror valueTypeValue = (ValueTypeValueMirror) value;
			writeByte((byte) (valueTypeValue.isEnum() ? 1 : 0));
			writeId(value.type());
			Value[] fieldValues = valueTypeValue.fieldValues();
			writeInt(fieldValues.length);
			for(Value fieldValue : fieldValues)
			{
				writeValue(fieldValue);
			}
		}
		else if(value instanceof ObjectValueMirror)
		{
			writeByte(SignatureConstants.ELEMENT_TYPE_OBJECT);
			writeInt(((ObjectValueMirror) value).id());
		}
		else if(value instanceof NoObjectValueMirror)
		{
			writeByte((byte) VALUE_TYPE_ID_NULL);
		}
		else
		{
			throw new IllegalArgumentException(value.getClass().getName());
		}
	}

	public void writeNumberValue(int tag, Number boxed)
	{
		writeByte((byte) tag);
		switch(tag)
		{
			case SignatureConstants.ELEMENT_TYPE_U1:
				writeInt(boxed.byteValue());
				break;
			case SignatureConstants.ELEMENT_TYPE_I1:
				writeInt(boxed.byteValue());
				break;
			case SignatureConstants.ELEMENT_TYPE_U2:
				writeInt(boxed.shortValue());
				break;
			case SignatureConstants.ELEMENT_TYPE_I2:
				writeInt(boxed.shortValue());
				break;
			case SignatureConstants.ELEMENT_TYPE_U4:
				writeInt(boxed.intValue());
				break;
			case SignatureConstants.ELEMENT_TYPE_I4:
				writeInt(boxed.intValue());
				break;
			case SignatureConstants.ELEMENT_TYPE_U8:
			case SignatureConstants.ELEMENT_TYPE_PTR:
				writeLong(boxed.longValue());
				break;
			case SignatureConstants.ELEMENT_TYPE_I8:
				writeLong(boxed.longValue());
				break;
			case SignatureConstants.ELEMENT_TYPE_R4:
				writeFloat(boxed.floatValue());
				break;
			case SignatureConstants.ELEMENT_TYPE_R8:
				writeDouble(boxed.doubleValue());
				break;
			default:
				throw new UnsupportedOperationException("Tag 0x" + Integer.toHexString(tag) + " is not handled");
		}
	}

	@Deprecated
	void writeObjectRef(long data)
	{
		writeInt((int) data);
	}

	@Deprecated
	void writeClassRef(long data)
	{
		writeInt((int) data);
	}

	void writeByteArray(byte[] data)
	{
		dataStream.write(data, 0, data.length);
	}

	public void writeString(String string)
	{
		try
		{
			byte[] stringBytes = string.getBytes("UTF8");
			writeInt(stringBytes.length);
			writeByteArray(stringBytes);
		}
		catch(java.io.UnsupportedEncodingException e)
		{
			throw new InternalException("Cannot convert string to UTF8 bytes");
		}
	}

	/**
	 * @return unsigned byte
	 */
	public int readUByte()
	{
		byte ret = pkt.data[inCursor];
		inCursor += 1;
		return ret & 0xFF;
	}

	/**
	 * Read byte represented as one bytes.
	 */
	public byte readByte()
	{
		byte ret = pkt.data[inCursor];
		inCursor += 1;
		return ret;
	}

	public boolean readByteBool()
	{
		int ret = readByte();
		return (ret != 0);
	}

	/**
	 * Read short represented as two bytes.
	 */
	short readShort()
	{
		int b1, b2;

		b1 = pkt.data[inCursor++] & 0xff;
		b2 = pkt.data[inCursor++] & 0xff;

		return (short) ((b1 << 8) + b2);
	}

	/**
	 * Read int represented as four bytes.
	 */
	public int readInt()
	{
		int b1, b2, b3, b4;

		b1 = pkt.data[inCursor++] & 0xff;
		b2 = pkt.data[inCursor++] & 0xff;
		b3 = pkt.data[inCursor++] & 0xff;
		b4 = pkt.data[inCursor++] & 0xff;

		return ((b1 << 24) + (b2 << 16) + (b3 << 8) + b4);
	}

	/**
	 * Read long represented as eight bytes.
	 */
	public long readLong()
	{
		long b1, b2, b3, b4;
		long b5, b6, b7, b8;

		b1 = pkt.data[inCursor++] & 0xff;
		b2 = pkt.data[inCursor++] & 0xff;
		b3 = pkt.data[inCursor++] & 0xff;
		b4 = pkt.data[inCursor++] & 0xff;

		b5 = pkt.data[inCursor++] & 0xff;
		b6 = pkt.data[inCursor++] & 0xff;
		b7 = pkt.data[inCursor++] & 0xff;
		b8 = pkt.data[inCursor++] & 0xff;

		return ((b1 << 56) + (b2 << 48) + (b3 << 40) + (b4 << 32) + (b5 << 24) + (b6 << 16) + (b7 << 8) + b8);
	}

	/**
	 * Read float represented as four bytes.
	 */
	float readFloat()
	{
		return Float.intBitsToFloat(readInt());
	}

	/**
	 * Read double represented as eight bytes.
	 */
	double readDouble()
	{
		return Double.longBitsToDouble(readLong());
	}

	/**
	 * Read string represented as four byte length followed by
	 * characters of the string.
	 */
	public String readString()
	{
		String ret;
		int len = readInt();

		try
		{
			ret = new String(pkt.data, inCursor, len, "UTF8");
		}
		catch(java.io.UnsupportedEncodingException e)
		{
			System.err.println(e);
			ret = "Conversion error!";
		}
		inCursor += len;
		return ret;
	}

	@Deprecated
	public int readId()
	{
		return readInt();
	}

	@Deprecated
	int readClassRef()
	{
		return readInt();
	}

	@Nonnull
	public ThreadMirror readThreadMirror()
	{
		int ref = readId();
		return new ThreadMirror(vm, ref);
	}


	@Nullable
	@Deprecated
	public MethodMirror readMethodMirror()
	{
		return null;
	}

	@Nullable
	public TypeRef readTypeRef()
	{
		boolean valid = readByteBool();
		if(!valid)
		{
			return null;
		}
		String moduleName = readString();
		int classId = readInt();
		boolean isPointer = readByteBool();
		boolean isByRef = readByteBool();
		int arraySizesLen = readByte();
		List<Integer> arraySizes = arraySizesLen == 0 ? null : new ArrayList<Integer>(arraySizesLen);
		for(int i = 0; i < arraySizesLen; i++)
		{
			arraySizes.add(readInt());
		}
		int arrayLowerBoundsLen = readByte();
		List<Integer> arrayLowerBounds = arrayLowerBoundsLen == 0 ? null : new ArrayList<Integer>(arrayLowerBoundsLen);
		for(int i = 0; i < arrayLowerBoundsLen; i++)
		{
			arrayLowerBounds.add(readInt());
		}
		return new TypeRef(moduleName, classId, isPointer, isByRef, arraySizes, arrayLowerBounds);
	}

	public void writeTypeRef(@Nonnull TypeRef typeRef)
	{
		writeByteBool(true);
		writeString(typeRef.getModuleName());
		writeInt(typeRef.getClassToken());
		writeByteBool(typeRef.isPointer());
		writeByteBool(typeRef.isByRef());

		List<Integer> arraySizes = typeRef.getArraySizes();
		if(arraySizes != null)
		{
			writeByte((byte) arraySizes.size());
			for(int arraySize : arraySizes)
			{
				writeInt(arraySize);
			}
		}
		else
		{
			writeByte((byte) 0);
		}

		List<Integer> arrayLowerBounds = typeRef.getArrayLowerBounds();
		if(arrayLowerBounds != null)
		{
			writeByte((byte) arrayLowerBounds.size());
			for(int arraySize : arrayLowerBounds)
			{
				writeInt(arraySize);
			}
		}
		else
		{
			writeByte((byte) 0);
		}
	}

	@Nullable
	@Deprecated
	public TypeMirror readTypeMirror()
	{
		return readTypeMirror(null);
	}

	@Nullable
	@Deprecated
	public TypeMirror readTypeMirror(@Nullable TypeMirror parent)
	{
		return null;
	}

	@Nonnull
	public ObjectValueMirror readObjectMirror()
	{
		int ref = readInt();
		long address = readLong();
		TypeRef typeRef = readTypeRef();
		return new ObjectValueMirror(vm, ref, address, typeRef);
	}

	@Nonnull
	public Value readValue()
	{
		int tag = readUByte();
		switch(tag)
		{
			case SignatureConstants.ELEMENT_TYPE_VOID:
				return new VoidValueMirror(vm);
			case SignatureConstants.ELEMENT_TYPE_BOOLEAN:
				return new BooleanValueMirror(vm, readByteBool());
			case SignatureConstants.ELEMENT_TYPE_I1:
			case SignatureConstants.ELEMENT_TYPE_U1:
				return new NumberValueMirror(vm, tag, readInt());
			case SignatureConstants.ELEMENT_TYPE_U2:
			case SignatureConstants.ELEMENT_TYPE_I2:
				return new NumberValueMirror(vm, tag, readInt());
			case SignatureConstants.ELEMENT_TYPE_U4:
			case SignatureConstants.ELEMENT_TYPE_I4:
				return new NumberValueMirror(vm, tag, readInt());
			case SignatureConstants.ELEMENT_TYPE_U8:
			case SignatureConstants.ELEMENT_TYPE_I8:
			case SignatureConstants.ELEMENT_TYPE_PTR:
				return new NumberValueMirror(vm, tag, readLong());
			case SignatureConstants.ELEMENT_TYPE_R4:
				return new NumberValueMirror(vm, tag, readFloat());
			case SignatureConstants.ELEMENT_TYPE_R8:
				return new NumberValueMirror(vm, tag, readDouble());
			case SignatureConstants.ELEMENT_TYPE_STRING:
			{
				int id = readInt();
				String value = readString();
				return new StringValueMirror(vm, id, value);
			}
			case SignatureConstants.ELEMENT_TYPE_CHAR:
				return new CharValueMirror(vm, (char) readInt());
			case SignatureConstants.ELEMENT_TYPE_VALUETYPE:
			{
				int id = readInt();
				long address = readLong();
				TypeRef typeRef = readTypeRef();
				boolean isEnum = readByteBool();
				int fieldCount = readInt();
				Value[] values = new Value[fieldCount];
				for(int i = 0; i < fieldCount; i++)
				{
					values[i] = readValue();
				}
				TypeMirror typeMirror = new TypeMirror(vm, typeRef);
				if(isEnum)
				{
					return new EnumValueMirror(id, address, vm, typeMirror, values);
				}
				return new StructValueMirror(id, address, vm, typeMirror, values);
			}
			case SignatureConstants.ELEMENT_TYPE_CLASS:
			case SignatureConstants.ELEMENT_TYPE_OBJECT:
				return readObjectMirror();
			case SignatureConstants.ELEMENT_TYPE_ARRAY:
			case SignatureConstants.ELEMENT_TYPE_SZARRAY:
				ObjectValueMirror valueMirror = readObjectMirror();
				int length = readInt();
				return new ArrayValueMirror(vm, length, valueMirror);
			case VALUE_TYPE_ID_NULL:
				return new NoObjectValueMirror(vm);
			//case VALUE_TYPE_ID_TYPE:
			//	return new TypeValueMirror(vm, readTypeMirror());
			default:
				throw new IllegalArgumentException("Unsupported tag: 0x" + Integer.toHexString(tag));
		}
	}

	@Nonnull
	public String[] readCustomAttributes()
	{
		int size = readInt();
		String[] array = new String[size];
		for(int i = 0; i < size; i++)
		{
			array[i] = readString();
		}
		return array;
	}

	byte[] readByteArray(int length)
	{
		byte[] array = new byte[length];
		System.arraycopy(pkt.data, inCursor, array, 0, length);
		inCursor += length;
		return array;
	}

	int skipBytes(int n)
	{
		inCursor += n;
		return n;
	}

	byte command()
	{
		return (byte) pkt.cmd;
	}
}
