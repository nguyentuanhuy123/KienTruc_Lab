package main

import (
	"fmt"
	"net/http"
)

func main() {
	http.HandleFunc("/", func(w http.ResponseWriter, r *http.Request) {
		fmt.Fprintf(w, "Hello, Docker Go! Chuc mung ban da den voi bai 5.")
	})

	fmt.Println("Server đang chạy tại cổng 8080...")
	http.ListenAndServe(":8080", nil)
}