package ui

func handleCommand(cmd string) {
	switch cmd {

	case "COPY_KEY":
		Menu.CopyKey()

	case "REGENERATE_KEY":
		Menu.RegenerateKey()

	case "EXIT":
		Menu.Shutdown()

	default:
		println("Unknown tray command:", cmd)
	}
}
