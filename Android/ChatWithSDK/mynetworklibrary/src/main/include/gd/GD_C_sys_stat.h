/*
 * (c) 2016 Good Technology Corporation. All rights reserved.
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
     * GD_UNISTD_fstat() is identical to GD_stat(), except that the file to be
     * stat-ed is specified by the file descriptor fd.
     *
     * @param fd <tt>int</tt> file descriptor which specifies file to be stat-ed.\n
     *
     * @param buf <tt>struct stat*</tt> buffer in which to write the stat data.\n
     *
     * @return <tt>int</tt> 0 on success, -1 on failure.\n
     */
    GD_C_API int GD_UNISTD_fstat(int fd, struct stat *buf);

#ifdef __cplusplus
}
#endif

/** @}
 */
