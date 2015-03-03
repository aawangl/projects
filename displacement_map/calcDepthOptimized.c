// CS 61C Fall 2014 Project 3

// include SSE intrinsics
#if defined(_MSC_VER)
#include <intrin.h>
#elif defined(__GNUC__) && (defined(__x86_64__) || defined(__i386__))
#include <x86intrin.h>
#endif

#include "calcDepthOptimized.h"
#include "calcDepthNaive.h"

#define ABS(x) (((x) < 0) ? (-(x)) : (x))

void calcDepthOptimized(float *depth, float *left, float *right, int imageWidth, int imageHeight, int featureWidth, int featureHeight, int maximumDisplacement)
{
	for (int y = 0; y < imageHeight; y++)
	{
		for (int x = 0; x < imageWidth; x++)
		{
			if ((y < featureHeight) || (y >= imageHeight - featureHeight) || (x < featureWidth) || (x >= imageWidth - featureWidth))
			{
				depth[y * imageWidth + x] = 0;
				continue;
			}

			float minimumSquaredDifference = -1;
			int minimumDy = 0;
			int minimumDx = 0;

			for (int dy = -maximumDisplacement; dy <= maximumDisplacement; dy++)
			{
				if (y + dy - featureHeight < 0 || y + dy + featureHeight >= imageHeight) 
				{
					continue;
				}
				for (int dx = -maximumDisplacement; dx <= maximumDisplacement; dx++)
				{
					if (x + dx - featureWidth < 0 || x + dx + featureWidth >= imageWidth)
					{
						continue;
					}

					float squaredDifference = 0;
					__m128 sum = _mm_setzero_ps();

					int boxX;
					for (boxX = -featureWidth; boxX < featureWidth / 4 * 4; boxX+=4)
					{
						for (int boxY = -featureHeight; boxY <= featureHeight; boxY++)
						{
							int leftX = x + boxX;
							int leftY = y + boxY;
							int rightX = x + dx + boxX;
							int rightY = y + dy + boxY;

							__m128 diff = _mm_sub_ps(_mm_loadu_ps(left + leftY * imageWidth + leftX), _mm_loadu_ps(right + rightY * imageWidth + rightX));
							diff = _mm_mul_ps(diff, diff);
							sum = _mm_add_ps(sum, diff);
						}
					}
					float store[4] = {0, 0, 0, 0};
					_mm_storeu_ps(store, sum);

					for (int i = 0; i < 4; i++) 
					{
						squaredDifference += store[i];
					}

					// tail case
					if (minimumSquaredDifference == -1 || squaredDifference <= minimumSquaredDifference)
					{
						if (featureWidth % 2 == 0) 
						{
							int boxX = featureWidth;
							for (int boxY = -featureHeight; boxY <= featureHeight; boxY++) 
							{
								int leftX = x + boxX;
								int leftY = y + boxY;
								int rightX = x + dx + boxX;
								int rightY = y + dy + boxY;

								float d1 = left[leftY * imageWidth + leftX] - right[rightY * imageWidth + rightX];

								squaredDifference += d1 * d1;
							}
						}
						else 
						{
							// for (; boxX <= featureWidth; boxX++) 
							// {
							// 	for (int boxY = -featureHeight; boxY <= featureHeight; boxY++)
							// 	{
							// 		int leftX = x + boxX;
							// 		int leftY = y + boxY;
							// 		int rightX = x + dx + boxX;
							// 		int rightY = y + dy + boxY;

							// 		float d1 = left[leftY * imageWidth + leftX] - right[rightY * imageWidth + rightX];

							// 		squaredDifference += d1 * d1;
							// 	}
							// }
							for (int boxX = featureWidth - 2; boxX <= featureWidth; boxX++) 
							{
								for (int boxY = -featureHeight; boxY <= featureHeight; boxY++) 
								{
									int leftX = x + boxX;
									int leftY = y + boxY;
									int rightX = x + dx + boxX;
									int rightY = y + dy + boxY;

									float d1 = left[leftY * imageWidth + leftX] - right[rightY * imageWidth + rightX];

									squaredDifference += d1 * d1;
								}
							}
						}
					}

					if ((minimumSquaredDifference == -1) || ((minimumSquaredDifference == squaredDifference) && (displacementNaive(dx, dy) < displacementNaive(minimumDx, minimumDy))) || (minimumSquaredDifference > squaredDifference))
					{
						minimumSquaredDifference = squaredDifference;
						minimumDx = dx;
						minimumDy = dy;
					}
				}
			}

			if (minimumSquaredDifference != -1)
			{
				if (maximumDisplacement == 0)
				{
					depth[y * imageWidth + x] = 0;
				}
				else
				{
					depth[y * imageWidth + x] = displacementNaive(minimumDx, minimumDy);
				}
			}
			else
			{
				depth[y * imageWidth + x] = 0;
			}
		}
	}
}
