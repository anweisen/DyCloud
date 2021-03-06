package net.anweisen.cloud.driver.network.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.ByteProcessor;
import net.anweisen.cloud.driver.network.exception.SilentDecoderException;

import javax.annotation.Nonnull;
import java.util.List;

public final class NettyPacketLengthDeserializer extends ByteToMessageDecoder {

	@Override
	protected void decode(@Nonnull ChannelHandlerContext context, @Nonnull ByteBuf buffer, @Nonnull List<Object> out) {
		if (!context.channel().isActive()) {
			buffer.clear();
			return;
		}

		VarIntByteProcessor processor = new VarIntByteProcessor();
		int varIntByteEnding = buffer.forEachByte(processor);

		if (processor.result != VarIntByteProcessor.ProcessingResult.OK) {
			throw SilentDecoderException.forInvalidVarInt();
		} else {
			int varInt = processor.varInt;
			int bytesRead = processor.bytesRead;

			if (varInt < 0) {
				buffer.clear();
				throw SilentDecoderException.forBadPacketLength();
			} else if (varInt == 0) {
				// empty packet, ignore it
				buffer.readerIndex(varIntByteEnding + 1);
			} else {
				int minimumReadableBytes = varInt + bytesRead;
				if (buffer.isReadable(minimumReadableBytes)) {
					out.add(buffer.retainedSlice(varIntByteEnding + 1, varInt));
					buffer.skipBytes(minimumReadableBytes);
				}
			}
		}
	}

	private static final class VarIntByteProcessor implements ByteProcessor {

		private int varInt;
		private int bytesRead;
		private ProcessingResult result;

		public VarIntByteProcessor() {
			result = ProcessingResult.TOO_SHORT;
		}

		@Override
		public boolean process(byte value) throws Exception {
			varInt |= (value & 0x7F) << bytesRead++ * 7;
			if (bytesRead > 5) {
				result = ProcessingResult.TOO_BIG;
				return false;
			} else if ((value & 0x80) != 128) {
				result = ProcessingResult.OK;
				return false;
			} else {
				return true;
			}
		}

		private enum ProcessingResult {
			OK,
			TOO_SHORT,
			TOO_BIG
		}
	}
}