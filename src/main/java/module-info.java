/**
 * @author VISTALL
 * @since 2020-10-22
 */
module mssdw.java.client {
    requires consulo.annotation;
    requires transitive consulo.internal.dotnet.asm;

    exports mssdw.connect;
    exports mssdw.event;
    exports mssdw.request;
    exports mssdw.util;
}