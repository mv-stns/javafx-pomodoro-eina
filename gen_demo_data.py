import os
import random
from datetime import datetime, timedelta
import json

BASE_DIR = "tomodoro_data"
CATEGORIES = ["Arbeit", "Studium", "Projekt", "Sport", "Sprachen", "Programmieren"]
MOODS = ["😔", "😐", "😊"]

def create_session_data(timestamp, is_completed=True):
    start_time = timestamp
    end_time = start_time + timedelta(minutes=25) if is_completed else start_time + timedelta(minutes=random.randint(5, 20))
    
    session_data = {
        "startTime": start_time.isoformat(),
        "endTime": end_time.isoformat(),
        "status": "COMPLETED" if is_completed else "INTERRUPTED",
        "mood": random.choice(MOODS),
        "phase": "FOCUS",
        "categories": ",".join(random.sample(CATEGORIES, random.randint(1, 2))),
        "targetDuration": "1500",
        "actualDuration": "1500" if is_completed else str(random.randint(300, 1200)),
        "completed": str(is_completed).lower()
    }
    
    notes = []
    selected_categories = session_data["categories"].split(",")
    
    if "ENIA" in selected_categories:
        notes.append("- ENIA Vorlesung nachgearbeitet")
        notes.append("- Übungsaufgaben gelöst")
    if "Sem3" in selected_categories:
        notes.append("- Präsentation vorbereitet")
        notes.append("- Literatur recherchiert")
    if "Java" in selected_categories:
        notes.append("- JavaFX Implementierung")
        notes.append("- Unit Tests geschrieben")
    
    notes = "\n".join(notes) if notes else ""
    
    return session_data, notes

def generate_demo_data(days_back=7):
    current_date = datetime.now()
    
    for day_offset in range(days_back):
        current_day = current_date - timedelta(days=day_offset)
        date_str = current_day.strftime("%Y-%m-%d")
        day_dir = os.path.join(BASE_DIR, date_str)
        os.makedirs(day_dir, exist_ok=True)
        
        # Generate 3-8 sessions per day
        num_sessions = random.randint(3, 8)
        for session_num in range(num_sessions):
            # Create random time for this session
            session_time = current_day.replace(
                hour=random.randint(9, 20),
                minute=random.randint(0, 59),
                second=random.randint(0, 59)
            )
            
            # 80% chance of completed session
            is_completed = random.random() < 0.8
            
            session_data, notes = create_session_data(session_time, is_completed)
            
            # Create session directory
            session_dir = os.path.join(day_dir, session_time.strftime("%H-%M-%S"))
            os.makedirs(session_dir, exist_ok=True)
            
            # Write session.txt
            with open(os.path.join(session_dir, "session.txt"), "w", encoding='utf-8') as f:
                for key, value in session_data.items():
                    f.write(f"{key}={value}\n")
            
            # Write notes.txt
            with open(os.path.join(session_dir, "notes.txt"), "w", encoding='utf-8') as f:
                f.write(notes)

if __name__ == "__main__":
    print("Generating demo data...")
    os.makedirs(BASE_DIR, exist_ok=True)
    generate_demo_data()
    print("Demo data generated successfully!")
