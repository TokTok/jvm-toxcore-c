#include "util/logging.h"

#include <iostream>
#include <iomanip>
#include <iterator>


std::size_t scope_counter::scope;

std::ostream &
scope_indent (std::ostream &os, int line)
{
  for (std::size_t i = 0; i < scope_counter::scope - (line >= 1000); i++)
    os << ' ';
  return os;
}


void
output_hex (std::ostream &os, uint8_t const *data, size_t length)
{
  os << '[';
  for (size_t i = 0; i < length; i++)
    os << format ("%02x", data[i]);
  os << ']';
}


std::ostream &
operator << (std::ostream &os, formatter const &fmt)
{
  std::copy (fmt.text_.cbegin (), fmt.text_.cend (), std::ostream_iterator<char> (os));
  return os;
}
