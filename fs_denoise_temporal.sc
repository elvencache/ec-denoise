$input v_texcoord0

/*
* Copyright 2020 elven cache. All rights reserved.
* License: https://github.com/bkaradzic/bgfx#license-bsd-2-clause
*/

#include "../common/common.sh"
#include "parameters.sh"
#include "normal_encoding.sh"
#include "shared_functions.sh"

SAMPLER2D(s_color,      0);
SAMPLER2D(s_normal,     1);
SAMPLER2D(s_velocity,   2);
SAMPLER2D(s_previous,   3); // previous color
SAMPLER2D(s_normalPrev, 4); // previous normal

#define COS_PI_OVER_4   0.70710678118

void main()
{
	vec2 texCoord = v_texcoord0;
    
    // read center pixel
	vec4 color = texture2D(s_color, texCoord);
    vec3 normal = NormalDecode(texture2D(s_normal, texCoord).xyz);
    
    // offset to last pixel
    vec2 velocity = texture2D(s_velocity, texCoord).xy;
	vec2 texCoordPrev = GetTexCoordPrevious(texCoord, velocity);
    
    // SVGF approach suggests sampling and test/rejecting 4 contributing
    // samples individually and then doing custom bilinear filter of result
    
    // multiply texCoordPrev by dimensions to get nearest pixels
    vec2 screenPixelPrev = texCoordPrev * u_viewRect.zw;
    vec2 screenPixelMin = floor(screenPixelPrev);
    vec2 screenPixelMix = fract(screenPixelPrev);
    
    vec2 coords[4];
    coords[0] = (screenPixelMin + vec2(0.0, 0.0)) * u_viewTexel.xy;
    coords[1] = (screenPixelMin + vec2(1.0, 0.0)) * u_viewTexel.xy;
    coords[2] = (screenPixelMin + vec2(0.0, 1.0)) * u_viewTexel.xy;
    coords[3] = (screenPixelMin + vec2(1.0, 1.0)) * u_viewTexel.xy;
    float coordWeights[4];
    vec4 sampleColors[4];
    
    // SVGF paper mentions comparing depths and normals to establish
    // whether samples are similar enough to contribute, but does not
    // describe how. References the following paper, which uses threshold
    // of cos(PI/4) to accept/reject.
    // https://software.intel.com/content/www/us/en/develop/articles/streaming-g-buffer-compression-for-multi-sample-anti-aliasing.html
    
    // haven't implemented depth comparison yet, this results in
    // values blurring across surfaces they shouldn't...
    
    for (uint i = 0; i < 4; ++i)
    {
        vec3 sampleNormal = NormalDecode(texture2D(s_normalPrev, coords[i]).xyz);
        float normalSimilarity = dot(normal, sampleNormal);
        float normalWeight = (normalSimilarity < COS_PI_OVER_4) ? 0.0 : 1.0;
        coordWeights[i] = normalWeight;
        
        sampleColors[i] = texture2D(s_previous, coords[i]);
    }
    
    // combine similarity weights with bilinear filter
    
    float x0 = 1.0 - screenPixelMix.x;
    float x1 = screenPixelMix.x;
    float y0 = 1.0 - screenPixelMix.y;
    float y1 = screenPixelMix.y;

    coordWeights[0] *= x0*y0;
    coordWeights[1] *= x1*y0;
    coordWeights[2] *= x0*y1;
    coordWeights[3] *= x1*y1;
    
    vec4 accumulatedColor = vec4_splat(0.0);
    float accumulatedWeight = 0.0;
    for (uint i = 0; i < 4; ++i)
    {
        accumulatedColor += sampleColors[i] * coordWeights[i];
        accumulatedWeight += coordWeights[i];
    }
    
    if (0.0 < accumulatedWeight)
    {
        accumulatedColor *= (1.0 / accumulatedWeight);
        color = mix(color, accumulatedColor, 0.8);
    }

	gl_FragColor = color;
}
