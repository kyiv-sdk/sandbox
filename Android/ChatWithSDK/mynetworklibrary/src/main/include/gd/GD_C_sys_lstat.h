/*
 * (c) 2018 BlackBerry Limited. All rights reserved.
 */

#pragma once

#include <sys/stat.h>

/** \addtogroup capilist
 * @{
 */

#ifdef __cplusplus
extern "C" {
#endif
    
#ifndef GD_C_API
#   if !defined(_WIN32)
#       define GD_C_API __attribute__((visibility("default")))
#   else
#       define GD_C_API
#   endif
#endif

    /** File statistics.
     * This function returns information about the file at a specified path. Read,
     * write or execute permission of the named file is not required, but all
     * directories listed in the path name leading to the file must be searchable.
     * GD_UNISTD_lstat() is identical to GD_stat(), except that if path is a symbolic link, then
     * the link itself is stat-ed, not the file that it refers to.
     *
     * @param path <tt>const char*</tt> pointer to a C string containing the path to the file.\n
     *
     * @param buf <tt>struct stat*</tt> buffer in which to write the stat data.\n
     *
     * @return <tt>int</tt> 0 on success, -1 on failure.\n
     */
    GD_C_API int GD_UNISTD_lstat(const char *path, struct stat *buf);

#ifdef __cplusplus
}
#endif

/** @}
 */
