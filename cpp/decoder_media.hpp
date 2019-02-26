#pragma once
#include "decoder.hpp"
#include "decoding_util.hpp"
#include <string>

namespace decoder {
	class DecoderMedia : public decoder::Decoder {
	public:
		DecoderMedia();
		std::string decode(int8_t* image);
	};
}