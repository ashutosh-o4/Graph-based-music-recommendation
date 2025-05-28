document.addEventListener('DOMContentLoaded', () => {
    const searchInput = document.getElementById('searchInput');
    const searchButton = document.getElementById('searchButton');
    const moodButtons = document.querySelectorAll('.mood-btn');
    const recommendationResults = document.getElementById('recommendationResults');
    const playlistResults = document.getElementById('playlistResults');
    const clearPlaylistButton = document.getElementById('clearPlaylist');

    // Generate a simple session ID (in a real app, this would come from the server)
    const sessionId = 'user_' + Math.random().toString(36).substr(2, 9);

    // Load playlist on page load
    loadPlaylist();

    // Clear playlist button
    clearPlaylistButton.addEventListener('click', clearPlaylist);

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
                <button class="add-to-playlist-btn" data-song-id="${song.songId}">Add to Playlist</button>
            `;
            resultsContainer.appendChild(songCard);
        });

        recommendationResults.appendChild(resultsContainer);

        // Add event listeners to "Add to Playlist" buttons
        document.querySelectorAll('.add-to-playlist-btn').forEach(button => {
            button.addEventListener('click', async (e) => {
                const songId = e.target.getAttribute('data-song-id');
                const song = songs.find(s => s.songId === songId);
                if (song) {
                    await addToPlaylist(song);
                }
            });
        });
    }

    // Function to display error messages
    function displayError(message) {
        recommendationResults.innerHTML = `
            <div class="error-message">
                <p>${message}</p>
            </div>
        `;
    }

    // Playlist functions
    async function loadPlaylist() {
        try {
            showLoading(playlistResults);
            const response = await fetch(`/playlist?sessionId=${sessionId}`);
            const data = await response.json();
            
            if (!response.ok) {
                throw new Error(data.error || 'Failed to load playlist');
            }
            
            displayPlaylist(data);
        } catch (error) {
            console.error('Error loading playlist:', error);
            displayPlaylistError(error.message);
        } finally {
            hideLoading(playlistResults);
        }
    }

    async function addToPlaylist(song) {
        try {
            const button = document.querySelector(`[data-song-id="${song.songId}"]`);
            if (button) {
                button.disabled = true;
                button.textContent = 'Adding...';
            }

            const response = await fetch(`/playlist/add?sessionId=${sessionId}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(song)
            });

            const data = await response.json();
            
            if (!response.ok) {
                throw new Error(data.error || 'Failed to add song to playlist');
            }

            if (data.success) {
                await loadPlaylist();
            } else {
                throw new Error(data.error || 'Failed to add song to playlist');
            }
        } catch (error) {
            console.error('Error adding to playlist:', error);
            alert(error.message);
        } finally {
            const button = document.querySelector(`[data-song-id="${song.songId}"]`);
            if (button) {
                button.disabled = false;
                button.textContent = 'Add to Playlist';
            }
        }
    }

    async function removeFromPlaylist(songId) {
        try {
            const button = document.querySelector(`[data-song-id="${songId}"]`);
            if (button) {
                button.disabled = true;
                button.textContent = 'Removing...';
            }

            const response = await fetch(`/playlist/remove?sessionId=${sessionId}&songId=${songId}`, {
                method: 'DELETE'
            });

            const data = await response.json();
            
            if (!response.ok) {
                throw new Error(data.error || 'Failed to remove song from playlist');
            }

            if (data.success) {
                await loadPlaylist();
            } else {
                throw new Error(data.error || 'Failed to remove song from playlist');
            }
        } catch (error) {
            console.error('Error removing from playlist:', error);
            alert(error.message);
        } finally {
            const button = document.querySelector(`[data-song-id="${songId}"]`);
            if (button) {
                button.disabled = false;
                button.textContent = 'Remove';
            }
        }
    }

    async function clearPlaylist() {
        try {
            clearPlaylistButton.disabled = true;
            clearPlaylistButton.textContent = 'Clearing...';

            const response = await fetch(`/playlist/clear?sessionId=${sessionId}`, {
                method: 'DELETE'
            });

            const data = await response.json();
            
            if (!response.ok) {
                throw new Error(data.error || 'Failed to clear playlist');
            }

            if (data.success) {
                await loadPlaylist();
            } else {
                throw new Error(data.error || 'Failed to clear playlist');
            }
        } catch (error) {
            console.error('Error clearing playlist:', error);
            alert(error.message);
        } finally {
            clearPlaylistButton.disabled = false;
            clearPlaylistButton.textContent = 'Clear Playlist';
        }
    }

    function displayPlaylistError(message) {
        playlistResults.innerHTML = `
            <div class="error-message">
                <p>${message}</p>
            </div>
        `;
    }

    function showLoading(element) {
        element.innerHTML = `
            <div class="loading-spinner">
                <div class="spinner"></div>
                <p>Loading...</p>
            </div>
        `;
    }

    function hideLoading(element) {
        const spinner = element.querySelector('.loading-spinner');
        if (spinner) {
            spinner.remove();
        }
    }

    function displayPlaylist(songs) {
        playlistResults.innerHTML = '';
        
        if (!songs || songs.length === 0) {
            playlistResults.innerHTML = '<p class="empty-playlist">Your playlist is empty</p>';
            return;
        }

        const playlistContainer = document.createElement('div');
        playlistContainer.className = 'playlist-container';

        songs.forEach(song => {
            const songCard = document.createElement('div');
            songCard.className = 'playlist-song-card';
            songCard.innerHTML = `
                <h3>${song.title || 'Unknown Title'}</h3>
                <p>Artist: ${song.artist || 'Unknown Artist'}</p>
                <button class="remove-from-playlist-btn" data-song-id="${song.songId}">Remove</button>
            `;
            playlistContainer.appendChild(songCard);
        });

        playlistResults.appendChild(playlistContainer);

        // Add event listeners to "Remove" buttons
        document.querySelectorAll('.remove-from-playlist-btn').forEach(button => {
            button.addEventListener('click', (e) => {
                const songId = e.target.getAttribute('data-song-id');
                removeFromPlaylist(songId);
            });
        });
    }
}); 