import os
import shutil

def clean_tomodoro_data():
    base_dir = "tomodoro_data"
    
    if os.path.exists(base_dir):
        try:
            shutil.rmtree(base_dir)
            print(f"Successfully deleted {base_dir} directory and all its contents.")
        except Exception as e:
            print(f"Error while deleting {base_dir}: {str(e)}")
    else:
        print(f"{base_dir} directory does not exist.")

if __name__ == "__main__":
    user_input = input("This will delete all data in the tomodoro_data directory. Are you sure? (y/n): ")
    if user_input.lower() == 'y':
        clean_tomodoro_data()
    else:
        print("Operation cancelled.")
