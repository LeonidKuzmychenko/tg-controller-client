package commands

import (
	"bytes"
	"desktop-tg-client/dto"
	"fmt"
	"image/png"
	"log"
	"sync"

	"github.com/kbinani/screenshot"
)

type CommandScreenshot struct {
	BaseCommand
}

func (c *CommandScreenshot) Condition() string {
	return "/screenshot"
}

func (c *CommandScreenshot) Run(string) dto.ResultInterface {
	listBytes, err := captureAllScreens()
	if err != nil {
		return dto.Result{Status: "Error"}
	}

	return dto.ResultImages{
		Result: dto.Result{Status: "Success"},
		Images: listBytes,
	}
}

func (c *CommandScreenshot) Analyze(cmd string) dto.ResultInterface {
	return c.BaseCommand.Analyze(cmd, c)
}

// --------------------- INTERNAL LOGIC ---------------------

func captureAllScreens() ([][]byte, error) {
	n := screenshot.NumActiveDisplays()
	if n == 0 {
		log.Println("[Screenshot] Мониторы не обнаружены")
		return nil, fmt.Errorf("no displays detected")
	}

	log.Printf("[Screenshot] Обнаружено мониторов: %d\n", n)

	results := make([][]byte, n) // заранее фиксируем размер
	errors := make([]error, n)

	var wg sync.WaitGroup
	wg.Add(n)

	for i := 0; i < n; i++ {
		monitorIndex := i

		go func() {
			defer wg.Done()

			log.Printf("[Screenshot] Начинаю обработку монитора %d", monitorIndex)

			bounds := screenshot.GetDisplayBounds(monitorIndex)

			img, err := screenshot.CaptureRect(bounds)
			if err != nil {
				log.Printf("[Screenshot] Ошибка снятия монитора %d: %v", monitorIndex, err)
				errors[monitorIndex] = err
				return
			}

			log.Printf("[Screenshot] Монитор %d успешно снят (%dx%d)",
				monitorIndex,
				bounds.Dx(), bounds.Dy(),
			)

			var buf bytes.Buffer
			if err := png.Encode(&buf, img); err != nil {
				log.Printf("[Screenshot] Ошибка PNG кодирования монитора %d: %v", monitorIndex, err)
				errors[monitorIndex] = err
				return
			}

			results[monitorIndex] = buf.Bytes()

			log.Printf("[Screenshot] Монитор %d PNG готов (%d байт)",
				monitorIndex,
				len(results[monitorIndex]),
			)
		}()
	}

	wg.Wait()

	// Проверяем ошибки
	for i, err := range errors {
		if err != nil {
			return nil, fmt.Errorf("error on monitor %d: %w", i, err)
		}
	}

	log.Println("[Screenshot] Все мониторы успешно обработаны")

	return results, nil
}
