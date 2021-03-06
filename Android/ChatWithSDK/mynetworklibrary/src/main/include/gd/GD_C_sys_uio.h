/*
 * (c) 2016 Good Technology Corporation. All rights reserved.
 */

#pragma once

#include <sys/uio.h>

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
    
    /** C API.
     */
    GD_C_API ssize_t	GD_readv(int, const struct iovec *, int);

    /** C API.
     */
    GD_C_API ssize_t	GD_writev(int, const struct iovec *, int);

#ifdef __cplusplus
}
#endif

/** @}
 */
