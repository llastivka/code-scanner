#pragma once
//#include "decoding_util.hpp"
#include <string>

class DecodingUtil {
public:
	int num;
	DecodingUtil(int num);
	std::string decode(int8_t* image);
};