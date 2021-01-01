# ec-denoise
bgfx example style project for denoising, work in progress

# building
1) setup bgfx
2) add these files to new folder in 'examples', like examples\xx-denoise
3) edit 'scripts\genie.lua' and 'examples\makefile' to add this new example to list of examples
4) run makefile in this folder to compile shaders

# notes
Implement SVGF style denoising as bgfx example. Goal is to explore various options and parameters, not produce an optimized, efficient denoiser.

Starts with deferred rendering scene with very basic lighting. Lighting is masked out with a noise pattern to provide something to denoise. There are two options for the noise pattern. One is a fixed 2x2 dither pattern to stand-in for lighting at quarter resolution. The other is the common shadertoy random pattern as a stand-in for some fancier lighting without enough samples per pixel, like ray tracing.

First a temporal denoising filter is applied. The temporal filter is only using normals to reject previous samples. The SVGF paper also describes using depth comparison to reject samples but that is not implemented here.

Followed by some number of spatial filters. These are implemented like in the SVGF paper. As an alternative to the 5x5 Edge-Avoiding A-Trous filter, can select a 3x3 filter instead. The 3x3 filter takes fewer samples and covers a smaller area, but takes less time to compute. From a loosely eyeballed comparison, N 5x5 passes looks similar to N+1 3x3 passes. The wider spatial filters take a fair chunk of time to compute. I wonder if it would be a good idea to interleave the input texture before computing, after the first pass which skips zero pixels.

I have not implemetened the variance guided part.

There's also an optional TXAA pass to be applied after. I am not happy with it's implementation yet, so it defaults to off here.

# references

Spatiotemporal Variance-Guided Filtering: Real-Time Reconstruction for Path-Traced Global Illumination.
by Christoph Schied and more.
 - SVGF denoising algorithm

Streaming G-Buffer Compression for Multi-Sample Anti-Aliasing.
by E. Kerzner and M. Salvi.
 - details about history comparison for temporal denoising filter

Edge-Avoiding À-Trous Wavelet Transform for Fast Global Illumination Filtering.
by Holger Dammertz and more.
 - details about a-trous algorithm for spatial denoising filter
