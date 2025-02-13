import requests

NODE_URL="http://localhost:8080"

def get_node_status():
    response = requests.get(NODE_URL+"/status")
    assert response.status_code == 200
    print(response.json())