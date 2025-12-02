package http

import (
	"bytes"
	"encoding/json"
	"fmt"
	"mime/multipart"
	"net/http"
	"net/url"
	"tg-controller-client/dto"
	"tg-controller-client/utils"
	"time"
)

type HttpRequests struct {
	client *http.Client
}

var HR = NewHttpRequests()

func NewHttpRequests() *HttpRequests {
	return &HttpRequests{
		client: &http.Client{
			Timeout: 10 * time.Second,
		},
	}
}

// ==================================================
//                 SEND TEXT
// ==================================================

func (h *HttpRequests) SendText(key, command, status string) {
	defer func() {
		if r := recover(); r != nil {
			fmt.Println("[HTTP] sendText panic:", r)
		}
	}()

	base := utils.HttpUrl()

	u := fmt.Sprintf(
		"%s/api/v1/answer/%s?command=%s&status=%s",
		base,
		key,
		url.QueryEscape(command),
		url.QueryEscape(status),
	)

	req, err := http.NewRequest("POST", u, bytes.NewBuffer([]byte{}))
	if err != nil {
		fmt.Println("[HTTP] SendText: NewRequest error:", err)
		return
	}

	_, err = h.client.Do(req)
	if err != nil {
		fmt.Println("[HTTP] SendText error:", err)
	}
}

// ==================================================
//                 SEND OBJECT (JSON)
// ==================================================

func (h *HttpRequests) SendObject(key, command string, result dto.ResultString) {
	defer func() {
		if r := recover(); r != nil {
			fmt.Println("[HTTP] sendObject panic:", r)
		}
	}()

	jsonData, err := json.Marshal(result)
	if err != nil {
		fmt.Println("[HTTP] JSON marshal error:", err)
		return
	}

	fmt.Println("Sending object to server:", string(jsonData))

	base := utils.HttpUrl()
	u := fmt.Sprintf(
		"%s/api/v1/answer/%s?command=%s&status=%s",
		base,
		key,
		url.QueryEscape(command),
		url.QueryEscape(result.Status),
	)

	req, err := http.NewRequest(
		"POST",
		u,
		bytes.NewBuffer(jsonData),
	)
	if err != nil {
		fmt.Println("[HTTP] SendObject: NewRequest error:", err)
		return
	}

	req.Header.Set("Content-Type", "application/json")

	_, err = h.client.Do(req)
	if err != nil {
		fmt.Println("[HTTP] SendObject error:", err)
	}
}

// ==================================================
//                 SEND IMAGE (PNG)
// ==================================================

func (h *HttpRequests) SendImagesMultipart(images [][]byte, key, command, status string) {
	defer func() {
		if r := recover(); r != nil {
			fmt.Println("[HTTP] SendImagesMultipart panic:", r)
		}
	}()

	base := utils.HttpUrl()

	u := fmt.Sprintf(
		"%s/api/v1/answer/%s?command=%s&status=%s",
		base,
		key,
		url.QueryEscape(command),
		url.QueryEscape(status),
	)

	var body bytes.Buffer
	writer := multipart.NewWriter(&body)

	// добавляем файлы
	for i, img := range images {
		fileName := fmt.Sprintf("image_%d.png", i)

		part, err := writer.CreateFormFile("files", fileName)
		if err != nil {
			fmt.Println("[HTTP] CreateFormFile error:", err)
			return
		}

		_, err = part.Write(img)
		if err != nil {
			fmt.Println("[HTTP] writing image error:", err)
			return
		}
	}

	// закрываем multipart writer (обязательно)
	writer.Close()

	req, err := http.NewRequest("POST", u, &body)
	if err != nil {
		fmt.Println("[HTTP] NewRequest error:", err)
		return
	}

	// самое главное — тип multipart/form-data
	req.Header.Set("Content-Type", writer.FormDataContentType())

	_, err = h.client.Do(req)
	if err != nil {
		fmt.Println("[HTTP] SendImagesMultipart error:", err)
	}
}
