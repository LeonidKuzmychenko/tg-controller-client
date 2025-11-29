package utils

import (
	"fmt"
	"os"
	"path/filepath"

	"github.com/google/uuid"
)

type KeyManager struct {
	key     string
	keyFile string
}

var KM = &KeyManager{} // аналог ENUM INSTANCE

// Init — инициализация KeyManager
func (km *KeyManager) Init() error {
	appData := os.Getenv("APPDATA")
	if appData == "" {
		return fmt.Errorf("APPDATA env var is empty")
	}

	configDir := filepath.Join(appData, "Desktop Control Telegram")
	km.keyFile = filepath.Join(configDir, "client.key")

	if err := os.MkdirAll(configDir, 0755); err != nil {
		return fmt.Errorf("cannot create config dir: %w", err)
	}

	key, err := km.loadOrGenerateKey()
	if err != nil {
		return fmt.Errorf("loadOrGenerateKey failed: %w", err)
	}

	km.key = key
	return nil
}

// Key — геттер
func (km *KeyManager) Key() string {
	return km.key
}

// RegenerateKey — пересоздаёт ключ и сохраняет на диск
func (km *KeyManager) RegenerateKey() (string, error) {
	newKey := km.generateKey()
	err := os.WriteFile(km.keyFile, []byte(newKey), 0644)
	if err != nil {
		return "", fmt.Errorf("unable to regenerate key: %w", err)
	}
	km.key = newKey
	return newKey, nil
}

func (km *KeyManager) loadOrGenerateKey() (string, error) {
	data, err := os.ReadFile(km.keyFile)
	if err == nil {
		existing := string(data)
		if len(existing) > 0 {
			return existing, nil
		}
	}
	return km.RegenerateKey()
}

func (km *KeyManager) generateKey() string {
	return uuid.New().String()
}
