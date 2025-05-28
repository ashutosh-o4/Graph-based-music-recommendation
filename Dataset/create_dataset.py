"""
Songs Dataset Generator

This script generates a clean dataset of Hindi and English songs with various metadata.
The output will be saved in CSV and JSON formats.

Author: Data Cleaning Assistant
"""

import csv
import json
import random

# Function to determine mood based on genre
def determine_mood(genre):
    """
    Derive a mood from the song genre.
    
    Args:
        genre (str): The genre of the song
        
    Returns:
        str: One of: 'Melancholy', 'Energetic', or 'Neutral'
    """
    if not genre or genre == "":
        return "Neutral"
    
    genre_str = str(genre).lower()
    if "sad" in genre_str or "melancholy" in genre_str:
        return "Melancholy"
    elif "dance" in genre_str or "energetic" in genre_str or "pop" in genre_str:
        return "Energetic"
    else:
        return "Neutral"

# Generate random tempo if missing
def generate_tempo():
    """
    Generate a random tempo value between 60 and 150 BPM.
    
    Returns:
        int: A random tempo value
    """
    return random.randint(60, 150)

# Define Hindi song templates
hindi_songs = [
    {"title": "Tum Hi Ho", "artist": "Arijit Singh", "genre": "Romantic", "year": 2013},
    {"title": "Channa Mereya", "artist": "Arijit Singh", "genre": "Sad", "year": 2016},
    {"title": "Agar Tum Saath Ho", "artist": "Alka Yagnik, Arijit Singh", "genre": "Sad", "year": 2015},
    {"title": "Dilbar", "artist": "Neha Kakkar", "genre": "Dance", "year": 2018},
    {"title": "Raabta", "artist": "Arijit Singh", "genre": "Romantic", "year": 2017},
    {"title": "Kalank Title Track", "artist": "Arijit Singh", "genre": "Romantic", "year": 2019},
    {"title": "Kar Gayi Chull", "artist": "Badshah, Fazilpuria", "genre": "Dance", "year": 2016},
    {"title": "Prem Ratan Dhan Payo", "artist": "Palak Muchhal", "genre": "DanceRomantic", "year": 2015},
    {"title": "Kamariya", "artist": "Darshan Raval", "genre": "Dance", "year": 2018},
    {"title": "Proper Patola", "artist": "Diljit Dosanjh, Badshah", "genre": "Dance", "year": 2018},
    {"title": "Apna Time Aayega", "artist": "Ranveer Singh", "genre": "Hip-Hop", "year": 2019},
    {"title": "Ae Dil Hai Mushkil", "artist": "Arijit Singh", "genre": "Romantic", "year": 2016},
    {"title": "Gerua", "artist": "Arijit Singh", "genre": "Romantic", "year": 2015},
    {"title": "Nashe Si Chadh Gayi", "artist": "Arijit Singh", "genre": "Dance", "year": 2016},
    {"title": "Tera Ban Jaunga", "artist": "Tulsi Kumar, Akhil Sachdeva", "genre": "Romantic", "year": 2019},
    {"title": "Paniyon Sa", "artist": "Atif Aslam", "genre": "Romantic", "year": 2018},
    {"title": "Bekhayali", "artist": "Sachet Tandon", "genre": "Sad", "year": 2019},
    {"title": "O Saathi", "artist": "Atif Aslam", "genre": "Romantic", "year": 2018},
    {"title": "Ghar More Pardesiya", "artist": "Shreya Ghoshal", "genre": "Classical", "year": 2019},
    {"title": "Kudiye Ni", "artist": "Aparshakti Khurana", "genre": "Dance", "year": 2019},
    {"title": "Teri Mitti", "artist": "B Praak", "genre": "Patriotic", "year": 2019},
    {"title": "Tujhe Kitna Chahne Lage", "artist": "Arijit Singh", "genre": "Romantic", "year": 2019},
    {"title": "Coca Cola", "artist": "Tony Kakkar, Neha Kakkar", "genre": "Dance", "year": 2019},
    {"title": "Lagdi Lahore Di", "artist": "Guru Randhawa", "genre": "Dance", "year": 2019},
    {"title": "Ghungroo", "artist": "Arijit Singh, Shilpa Rao", "genre": "Dance", "year": 2019},
    {"title": "Dheeme Dheeme", "artist": "Tony Kakkar", "genre": "Dance", "year": 2019},
    {"title": "Duniyaa", "artist": "Akhil, Dhvani Bhanushali", "genre": "Romantic", "year": 2019},
    {"title": "Leja Re", "artist": "Dhvani Bhanushali", "genre": "Romantic", "year": 2018},
    {"title": "Nain Na Jodeen", "artist": "Arijit Singh", "genre": "Romantic", "year": 2018},
    {"title": "Vaaste", "artist": "Dhvani Bhanushali", "genre": "Romantic", "year": 2019}
]

# Define English song templates
english_songs = [
    {"title": "Shape of You", "artist": "Ed Sheeran", "genre": "Pop", "year": 2017},
    {"title": "Someone You Loved", "artist": "Lewis Capaldi", "genre": "Pop", "year": 2019},
    {"title": "Blinding Lights", "artist": "The Weeknd", "genre": "Synth-pop", "year": 2020},
    {"title": "Bad Guy", "artist": "Billie Eilish", "genre": "Pop", "year": 2019},
    {"title": "Uptown Funk", "artist": "Mark Ronson ft. Bruno Mars", "genre": "Funk", "year": 2014},
    {"title": "Dance Monkey", "artist": "Tones and I", "genre": "Dance-pop", "year": 2019},
    {"title": "Hello", "artist": "Adele", "genre": "Soul", "year": 2015},
    {"title": "Watermelon Sugar", "artist": "Harry Styles", "genre": "Pop rock", "year": 2020},
    {"title": "Perfect", "artist": "Ed Sheeran", "genre": "Pop", "year": 2017},
    {"title": "Despacito", "artist": "Luis Fonsi ft. Daddy Yankee", "genre": "Reggaeton", "year": 2017},
    {"title": "God's Plan", "artist": "Drake", "genre": "Hip-Hop", "year": 2018},
    {"title": "Shallow", "artist": "Lady Gaga & Bradley Cooper", "genre": "Country-Rock", "year": 2018},
    {"title": "Closer", "artist": "The Chainsmokers ft. Halsey", "genre": "Pop", "year": 2016},
    {"title": "One Dance", "artist": "Drake ft. Wizkid & Kyla", "genre": "Dancehall", "year": 2016},
    {"title": "rockstar", "artist": "Post Malone ft. 21 Savage", "genre": "Hip-Hop", "year": 2017},
    {"title": "Old Town Road", "artist": "Lil Nas X ft. Billy Ray Cyrus", "genre": "Country-Rap", "year": 2019},
    {"title": "Thinking Out Loud", "artist": "Ed Sheeran", "genre": "Soul", "year": 2014},
    {"title": "Señorita", "artist": "Shawn Mendes & Camila Cabello", "genre": "Pop", "year": 2019},
    {"title": "Don't Start Now", "artist": "Dua Lipa", "genre": "Disco-Pop", "year": 2019},
    {"title": "Love Yourself", "artist": "Justin Bieber", "genre": "Pop", "year": 2015},
    {"title": "7 Rings", "artist": "Ariana Grande", "genre": "Pop", "year": 2019},
    {"title": "Havana", "artist": "Camila Cabello ft. Young Thug", "genre": "Pop", "year": 2017},
    {"title": "SAD!", "artist": "XXXTENTACION", "genre": "Hip-Hop", "year": 2018},
    {"title": "thank u, next", "artist": "Ariana Grande", "genre": "Pop", "year": 2018},
    {"title": "High Hopes", "artist": "Panic! At The Disco", "genre": "Pop", "year": 2018},
    {"title": "Sicko Mode", "artist": "Travis Scott", "genre": "Hip-Hop", "year": 2018},
    {"title": "Bohemian Rhapsody", "artist": "Queen", "genre": "Rock", "year": 2018},
    {"title": "Lucid Dreams", "artist": "Juice WRLD", "genre": "Hip-Hop", "year": 2018},
    {"title": "Circles", "artist": "Post Malone", "genre": "Pop", "year": 2019},
    {"title": "Memories", "artist": "Maroon 5", "genre": "Pop", "year": 2019}
]

def main():
    """
    Main function to generate the dataset.
    """
    # Generate Hindi songs dataset
    print("Generating Hindi songs...")
    hindi_dataset = []
    for i in range(500):
        template = random.choice(hindi_songs)
        
        # Sometimes use the original title, sometimes add a variation
        if i < len(hindi_songs) or random.random() < 0.3:
            song_title = template['title']
        else:
            variations = [" (Remix)", " - Unplugged", " 2.0", " (Cover)", " - Live", " (Reprise)"]
            song_title = template['title'] + random.choice(variations)
        
        # Create song entry with all required fields
        song = {
            "title": song_title,
            "artist": template['artist'],
            "language": "Hindi",
            "genre": template['genre'],
            "release_year": random.randint(max(2010, template['year'] - 2), min(2023, template['year'] + 3)),
            "mood": determine_mood(template['genre']),
            "tempo": generate_tempo(),
            "popularity": round(random.uniform(0.5, 0.98), 2)
        }
        
        hindi_dataset.append(song)

    print(f"Generated {len(hindi_dataset)} Hindi songs")

    # Generate English songs dataset
    print("Generating English songs...")
    english_dataset = []
    for i in range(500):
        template = random.choice(english_songs)
        
        # Sometimes use the original title, sometimes add a variation
        if i < len(english_songs) or random.random() < 0.3:
            song_title = template['title']
        else:
            variations = [" (Remix)", " - Acoustic", " ft. New Artist", " (Radio Edit)", " - Live", " (Extended)"]
            song_title = template['title'] + random.choice(variations)
        
        # Create song entry with all required fields
        song = {
            "title": song_title,
            "artist": template['artist'],
            "language": "English",
            "genre": template['genre'],
            "release_year": random.randint(max(2010, template['year'] - 2), min(2023, template['year'] + 3)),
            "mood": determine_mood(template['genre']),
            "tempo": generate_tempo(),
            "popularity": round(random.uniform(0.5, 0.98), 2)
        }
        
        english_dataset.append(song)

    print(f"Generated {len(english_dataset)} English songs")

    # Merge and create final output
    print("Creating final dataset...")
    all_songs = hindi_dataset + english_dataset
    random.shuffle(all_songs)

    # Limit to 1000 songs
    if len(all_songs) > 1000:
        all_songs = all_songs[:1000]

    # Add song_id
    for i, song in enumerate(all_songs):
        song["song_id"] = f"S{str(i+1).zfill(3)}"

    # Reorder fields to match required schema
    final_songs = []
    for song in all_songs:
        final_song = {
            "song_id": song["song_id"],
            "title": song["title"],
            "artist": song["artist"],
            "language": song["language"],
            "genre": song["genre"],
            "release_year": song["release_year"],
            "mood": song["mood"],
            "tempo": song["tempo"],
            "popularity": song["popularity"]
        }
        final_songs.append(final_song)

    # Save to CSV
    with open('songs_dataset.csv', 'w', newline='', encoding='utf-8') as f:
        writer = csv.DictWriter(f, fieldnames=final_songs[0].keys())
        writer.writeheader()
        writer.writerows(final_songs)

    print(f"Saved {len(final_songs)} songs to songs_dataset.csv")

    # Save to JSON
    with open('songs_dataset.json', 'w', encoding='utf-8') as f:
        json.dump(final_songs, f, indent=2, ensure_ascii=False)

    print(f"Saved {len(final_songs)} songs to songs_dataset.json")
    print("Dataset creation completed successfully!")

if __name__ == "__main__":
    main() 