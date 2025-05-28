# Songs Dataset

This dataset contains a collection of 1000 songs with metadata in both English and Hindi languages from 2010 onward.

## Dataset Structure

The data is available in two formats:
- `songs_dataset.csv` - CSV format
- `songs_dataset.json` - JSON format

Each song entry contains the following fields:

| Field | Description | Example |
|-------|-------------|---------|
| song_id | Unique identifier for the song | S001 |
| title | Title of the song | Tum Hi Ho |
| artist | Artist or performer of the song | Arijit Singh |
| language | Language of the song (English or Hindi) | Hindi |
| genre | Genre of the song | Romantic |
| release_year | Year the song was released | 2013 |
| mood | Mood of the song (Energetic, Melancholy, or Neutral) | Melancholy |
| tempo | Tempo of the song in beats per minute (60-150) | 65 |
| popularity | Popularity score normalized between 0 and 1 | 0.85 |

## Dataset Creation Details

The dataset was created with the following specifications:
- Contains 1000 songs total (mix of Hindi and English)
- All songs are from 2010-2023
- Mood was determined based on genre:
  - If genre contains "Sad" → mood = "Melancholy"
  - If genre contains "Dance" or "Pop" → mood = "Energetic"
  - Otherwise → mood = "Neutral"
- Tempo values range from 60 to 150 BPM
- Popularity scores are normalized between 0 and 1

## Regenerating the Dataset

The Python script `create_dataset.py` is included in this folder. You can run it to regenerate the dataset with new random values:

```
python create_dataset.py
```

This will create new versions of the CSV and JSON files. Note that since the script uses random values for some fields, the output will be different each time you run it.

## Example JSON Format

```json
{
  "song_id": "S001",
  "title": "Tum Hi Ho",
  "artist": "Arijit Singh",
  "language": "Hindi",
  "genre": "Romantic",
  "release_year": 2013,
  "mood": "Melancholy",
  "tempo": 65,
  "popularity": 0.85
}
```

## Usage

This dataset can be used for various music analysis tasks, recommendation systems, or any data science projects requiring music metadata. 