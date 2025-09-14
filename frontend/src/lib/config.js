// Get the base URL from environment variables
export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || '';

// Helper function for making API calls
export function apiUrl(endpoint) {
    return `${API_BASE_URL}${endpoint}`;
}

export const ENDPOINTS = {
    POLLS: '/polls',
    USERS: '/users',
    VOTES: '/votes'
};