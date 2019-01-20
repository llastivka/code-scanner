#include "decoder_media.hpp"
#include <iostream>
#include <stdint.h>

using namespace std;
using namespace decoder;

int main(int argc, char const *argv[])
{
	DecoderMedia decoder = DecoderMedia();
	int8_t arr[4];
	cout << decoder.decode(arr) << endl;
	return 0;
}