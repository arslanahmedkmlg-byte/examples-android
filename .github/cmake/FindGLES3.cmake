find_library(GLES3_LIBRARY NAMES GLESv3)

include(FindPackageHandleStandardArgs)
find_package_handle_standard_args(GLES3 DEFAULT_MSG GLES3_LIBRARY)

set(GLES3_LIBRARIES "${GLES3_LIBRARY}")
set(PROJECTM_OPENGL_LIBRARIES "${GLES3_LIBRARY}")
