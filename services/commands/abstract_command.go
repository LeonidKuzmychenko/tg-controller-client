package commands

import (
	"awesomeProject/dto"
)

// AbstractCommand — интерфейс как в Java (analyze, run, condition)
type AbstractCommand interface {
	Analyze(cmd string) dto.ResultInterface
	Run(cmd string) dto.ResultInterface
	Condition() string
	SetNext(next AbstractCommand)
	GetNext() AbstractCommand
}

// BaseCommand — базовая структура, аналог абстрактного класса в Java.
// Хранит "next" и общую логику Analyze().
type BaseCommand struct {
	next AbstractCommand
}

// SetNext — установка следующей команды
func (bc *BaseCommand) SetNext(next AbstractCommand) {
	bc.next = next
}

// GetNext — возврат next
func (bc *BaseCommand) GetNext() AbstractCommand {
	return bc.next
}

// Analyze — общая логика как в Java (chain of responsibility)
func (bc *BaseCommand) Analyze(cmd string, self AbstractCommand) dto.ResultInterface {
	if cmd == self.Condition() {
		return self.Run(cmd)
	}

	if bc.next != nil {
		return bc.next.Analyze(cmd)
	}

	return dto.Result{Status: "Unknown"}
}
