#include "decoder_media.hpp"

namespace decoder {
	std::shared_ptr<Decoder> Decoder::create() {
		return std::make_shared<DecoderMedia>();
	}

	DecoderMedia::DecoderMedia() {}

	std::string DecoderMedia::decode(int8_t* image) {
		DecodingUtil decodingUtil = DecodingUtil(100);
		return decodingUtil.decode(image);
	}
}