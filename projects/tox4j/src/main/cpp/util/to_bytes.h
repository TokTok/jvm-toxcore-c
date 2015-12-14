#pragma once

#include <vector>

static inline void
to_bytes (std::vector<uint8_t> &bytes, int16_t value)
{
  bytes.push_back (value >> 8);
  bytes.push_back (value & 0xff);
}


template<typename Iterator>
std::vector<uint8_t>
to_bytes (Iterator begin, Iterator end)
{
  std::vector<uint8_t> bytes;
  bytes.reserve ((end - begin) * sizeof (*begin));
  while (begin != end)
    {
      auto value = *begin;
      to_bytes (bytes, value);
      ++begin;
    }

  return bytes;
}
