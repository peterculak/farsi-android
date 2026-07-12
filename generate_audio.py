import os
import re
from gtts import gTTS

kt_file = "app/src/main/java/com/example/farsialphabet/Data.kt"
out_dir = "app/src/main/res/raw"

os.makedirs(out_dir, exist_ok=True)

with open(kt_file, "r", encoding="utf-8") as f:
    content = f.read()

pattern = r'FarsiLetter\((\d+),\s*"[^"]*",\s*"[^"]*",\s*"([^"]*)"'

for match in re.finditer(pattern, content):
    letter_id = match.group(1)
    full_form = match.group(2)
    
    # Try 'fa' (Persian), fallback to 'ar' (Arabic) if gTTS lacks 'fa'
    try:
        tts = gTTS(text=full_form, lang='ar') 
        tts.save(f"{out_dir}/letter_{letter_id}.mp3")
        print(f"Generated audio for letter {letter_id}: {full_form}")
    except Exception as e:
        print(f"Failed {letter_id}: {e}")

print("Done generating audio files.")
