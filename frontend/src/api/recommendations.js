import api from "./axios";
/**
 * Fetches book recommendations based on genre and sorting options.
 *
 * @param {Object} params - The parameters for fetching recommendations.
 * @param {number} [params.genreId] - The ID of the genre to filter recommendations.
 * @param {string} [params.sort] - The sorting order (e.g., 'title,asc').
 * @param {number} [params.page=0] - The page number for pagination.
 * @param {number} [params.size=6] - The number of recommendations per page.
 * @returns {Promise} - A promise that resolves to the API response.
 */
export function getRecommendations({genreId, sort, page = 0, size = 6}) {
    return api.get("/recommendations", {
        params: {
            ...(genreId && {genreId}),
            ...(sort && {sort}),
            page,
            size,
        },
    });
}
