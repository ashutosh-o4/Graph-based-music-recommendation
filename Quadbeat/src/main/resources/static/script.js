document.addEventListener('DOMContentLoaded', () => {
    const searchInput = document.getElementById('searchInput');
    const searchButton = document.getElementById('searchButton');
    const moodButtons = document.querySelectorAll('.mood-btn');
    const recommendationResults = document.getElementById('recommendationResults');

    // Search functionality
    searchButton.addEventListener('click', () => {
        const query = searchInput.value.trim();
        if (query) {
            searchSongs(query);
        }
    });

    searchInput.addEventListener('keypress', (e) => {
        if (e.key === 'Enter') {
            const query = searchInput.value.trim();
            if (query) {
                searchSongs(query);
            }
        }
    });

    // Mood-based recommendation
    moodButtons.forEach(button => {
        button.addEventListener('click', () => {
            const mood = button.getAttribute('data-mood');
            getMoodBasedRecommendations(mood);
            // Show loading state
            button.disabled = true;
            button.textContent = 'Loading...';
            
            // Reset other buttons
            moodButtons.forEach(btn => {
                if (btn !== button) {
                    btn.disabled = false;
                    btn.textContent = btn.getAttribute('data-mood');
                }
            });
        });
    });

    // Function to search songs
    async function searchSongs(query) {
        try {
            console.log('Searching for:', query);
            const response = await fetch(`/search?query=${encodeURIComponent(query)}`);
            
            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(errorText || `HTTP error! status: ${response.status}`);
            }
            
            const data = await response.json();
            
            if (data.message) {
                // No results found
                displayError(data.message);
                return;
            }
            
            if (data.length === 0) {
                displayError('No songs found matching your search.');
                return;
            }
            
            // Always get recommendations for the first result
            const song = data[0];
            await getRecommendations(song.songId);
        } catch (error) {
            console.error('Error searching songs:', error);
            displayError('Failed to search songs: ' + error.message);
        }
    }

    // Function to get recommendations based on songId
    async function getRecommendations(songId) {
        try {
            const response = await fetch(`/recommend?songId=${encodeURIComponent(songId)}&graphType=SONG_BASED&algorithm=BFS`);
            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(errorText || `HTTP error! status: ${response.status}`);
            }
            const recommendations = await response.json();
            displayResults(recommendations);
        } catch (error) {
            console.error('Error fetching recommendations:', error);
            displayError('Error getting recommendations. Please try again.');
        }
    }

    // Function to get mood-based recommendations
    async function getMoodBasedRecommendations(mood) {
        try {
            console.log('Requesting recommendations for mood:', mood);
            const response = await fetch(`/recommend?songId=${encodeURIComponent(mood)}&graphType=MOOD_BASED&algorithm=DIJKSTRA`);
            
            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(errorText || `HTTP error! status: ${response.status}`);
            }
            
            const data = await response.json();
            console.log('Received recommendations:', data);
            displayResults(data);
        } catch (error) {
            console.error('Error getting mood-based recommendations:', error);
            let errorMessage = error.message;
            if (errorMessage.includes('No songs found with mood')) {
                errorMessage = `No songs found with mood "${mood}". Please try a different mood.`;
            }
            displayError(errorMessage);
        } finally {
            // Reset all mood buttons
            moodButtons.forEach(btn => {
                btn.disabled = false;
                btn.textContent = btn.getAttribute('data-mood');
            });
        }
    }

    // Function to display results
    function displayResults(songs) {
        recommendationResults.innerHTML = '';
        
        if (!songs || songs.length === 0) {
            displayError('No songs found. Try a different mood or search term.');
            return;
        }

        const resultsHeader = document.createElement('div');
        resultsHeader.className = 'results-header';
        resultsHeader.innerHTML = `<h3>Found ${songs.length} recommendations:</h3>`;
        recommendationResults.appendChild(resultsHeader);

        const resultsContainer = document.createElement('div');
        resultsContainer.className = 'results-container';

        songs.forEach(song => {
            const songCard = document.createElement('div');
            songCard.className = 'song-card';
            songCard.innerHTML = `
                <h3>${song.title || 'Unknown Title'}</h3>
                <p>Artist: ${song.artist || 'Unknown Artist'}</p>
                ${song.score ? `<p>Score: ${song.score.toFixed(2)}</p>` : ''}
            `;
            resultsContainer.appendChild(songCard);
        });

        recommendationResults.appendChild(resultsContainer);
    }

    // Function to display error messages
    function displayError(message) {
        recommendationResults.innerHTML = `
            <div class="error-message">
                <p>${message}</p>
            </div>
        `;
    }
}); 