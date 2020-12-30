/*
* Copyright 2020 elven cache. All rights reserved.
* License: https://github.com/bkaradzic/bgfx#license-bsd-2-clause
*/

#ifndef PARAMETERS_SH
#define PARAMETERS_SH

uniform vec4 u_params[16];

#define u_jitterCurr				(u_params[3].xy)
#define u_jitterPrev				(u_params[3].zw)
#define u_applyMitchellFilter		(u_params[5].y)

#define u_worldToViewPrev0			(u_params[6])
#define u_worldToViewPrev1			(u_params[7])
#define u_worldToViewPrev2			(u_params[8])
#define u_worldToViewPrev3			(u_params[9])
#define u_viewToProjPrev0			(u_params[10])
#define u_viewToProjPrev1			(u_params[11])
#define u_viewToProjPrev2			(u_params[12])
#define u_viewToProjPrev3			(u_params[13])

#define u_frameIdx					(u_params[14].x)
#define u_noiseType					(u_params[14].y) // 0=none, 1=dither, 2=random
#define u_texCoordStep				(u_params[15].x)
#define u_sigmaDepth				(u_params[15].y)
#define u_sigmaNormal				(u_params[15].z)

#endif // PARAMETERS_SH
