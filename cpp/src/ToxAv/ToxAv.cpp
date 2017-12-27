#include "ToxAv.h"

using namespace av;

ToxInstances<tox::av_ptr, std::unique_ptr<Events>> av::instances;

template<> char const *module_name<ToxAV>() { return "av"; }
template<> char const *exn_prefix<ToxAV>() { return "av"; }

void
reference_symbols_av ()
{
  int toxav_finalize; // For Java only.

#define JAVA_METHOD_REF(NAME)  unused (JAVA_METHOD_NAME (NAME));
#define CXX_FUNCTION_REF(NAME) unused (NAME);
#include "generated/natives.h"
#undef CXX_FUNCTION_REF
#undef JAVA_METHOD_REF
}
