/*
* Copyright 2020 elven cache. All rights reserved.
* License: https://github.com/bkaradzic/bgfx#license-bsd-2-clause
*/

#ifndef SHARED_FUNCTIONS_SH
#define SHARED_FUNCTIONS_SH

vec2 GetTexCoordPrevious(vec2 texCoord, vec2 velocity)
{
	vec2 texCoordPrev = texCoord - velocity;
	vec2 jitterDelta = (u_jitterCurr-u_jitterPrev);

	if (u_applyJitterDelta > 0.0) {
		texCoordPrev += (jitterDelta) * u_viewTexel.xy; // * vec2(0.5, 0.5);
	}
    
    return texCoordPrev;
}

#endif // SHARED_FUNCTIONS_SH
