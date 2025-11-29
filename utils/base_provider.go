package utils

import (
	"bufio"
	_ "embed"
	"fmt"
	"strings"
)

// ------------------ EMBED ФАЙЛА ------------------

//go:embed config.properties
var configFile string

// ------------------ ГЛОБАЛЬНЫЕ ПЕРЕМЕННЫЕ ------------------

var (
	socketUrl string
	httpUrl   string
)

// ------------------ init() — АНАЛОГ static {} ------------------

func init() {
	props, err := loadInternalProperties()
	if err != nil {
		panic(fmt.Errorf("ошибка инициализации BaseProvider: %w", err))
	}

	socketUrl = props["socketUrl"]
	httpUrl = props["httpUrl"]
}

// ------------------ PUBLIC GETTERS ------------------

func Key() string {
	key := KM.Key()
	fmt.Println(key) // аналог логирования
	return key
}

func SocketUrl() string {
	return socketUrl
}

func HttpUrl() string {
	return httpUrl
}

// ------------------ INTERNAL LOGIC ------------------

func loadInternalProperties() (map[string]string, error) {
	props := make(map[string]string)

	scanner := bufio.NewScanner(strings.NewReader(configFile))
	for scanner.Scan() {
		line := strings.TrimSpace(scanner.Text())
		if line == "" || strings.HasPrefix(line, "#") {
			continue
		}

		kv := strings.SplitN(line, "=", 2)
		if len(kv) != 2 {
			continue
		}

		key := strings.TrimSpace(kv[0])
		value := strings.TrimSpace(kv[1])
		props[key] = value
	}

	if err := scanner.Err(); err != nil {
		return nil, fmt.Errorf("ошибка разбора config.properties: %w", err)
	}

	if props["socketUrl"] == "" || props["httpUrl"] == "" {
		return nil, fmt.Errorf("отсутствуют обязательные свойства socketUrl/httpUrl")
	}

	return props, nil
}
