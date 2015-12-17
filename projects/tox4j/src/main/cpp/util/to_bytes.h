#pragma once

#include <cstdint>
#include <string>

template<typename OutputIterator>
static OutputIterator
to_bytes (OutputIterator output, int16_t value)
{
  *output++ = value >> 8;
  *output++ = value & 0xff;
  return output;
}


template<typename Iterator>
void
to_bytes (Iterator begin, Iterator end, std::string &bytes)
{
  bytes.resize ((end - begin) * sizeof (*begin));

  auto output = bytes.begin ();
  while (begin != end)
    {
      auto value = *begin;
      output = to_bytes (output, value);
      ++begin;
    }
}
