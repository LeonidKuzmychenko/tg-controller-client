package commands

import (
	"tg-controller-client/dto"
)

// CommandShutdown — аналог Java CommandShutdown
type CommandShutdown struct {
	BaseCommand
}

func (c *CommandShutdown) Condition() string {
	return "/shutdown"
}

func (c *CommandShutdown) Run(cmd string) dto.ResultInterface {

	// --- если хочешь настоящий shutdown Windows ---
	// out := exec.Command("shutdown", "/s", "/f", "/t", "0").Start()
	// if out != nil {
	//     return dto.Result{Status: "Fail"}
	// }
	// return dto.Result{Status: "Success"}

	// Пока — как в твоём Java-коде:
	return dto.Result{Status: "Success"}
}

func (c *CommandShutdown) Analyze(cmd string) dto.ResultInterface {
	return c.BaseCommand.Analyze(cmd, c)
}
