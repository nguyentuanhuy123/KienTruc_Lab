#!/bin/bash
# Script test API nhanh

BASE="http://localhost:8080"
echo "=== Test Movie Ticket System API ==="

echo -e "\n[1] Register user..."
curl -s -X POST $BASE/api/users/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","email":"test@test.com","password":"123456","fullName":"Test User"}' | jq .

echo -e "\n[2] Login..."
TOKEN=$(curl -s -X POST $BASE/api/users/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"123456"}' | jq -r '.token')
echo "Token: $TOKEN"

echo -e "\n[3] Get movies..."
curl -s $BASE/api/movies -H "Authorization: Bearer $TOKEN" | jq .

echo -e "\n[4] Create booking..."
curl -s -X POST $BASE/api/bookings \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"userId":1,"username":"testuser","movieId":1,"movieTitle":"Avengers","seats":2,"pricePerSeat":120000}' | jq .

echo -e "\n[5] Get my bookings..."
sleep 3
curl -s $BASE/api/bookings/user/1 -H "Authorization: Bearer $TOKEN" | jq .

echo -e "\n=== Done! Check logs for events ==="
