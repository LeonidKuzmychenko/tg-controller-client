//go:build windows

package main

import (
	"syscall"
	"unsafe"
)

var (
	kernel32         = syscall.NewLazyDLL("kernel32.dll")
	procCreateMutexA = kernel32.NewProc("CreateMutexA")
	procGetLastError = kernel32.NewProc("GetLastError")
)

// ERROR_ALREADY_EXISTS = 183
const alreadyExists = 183

func ensureSingleInstance() bool {
	name := []byte("DesktopControlMutex\x00")

	handle, _, _ := procCreateMutexA.Call(
		0,
		1,
		uintptr(unsafe.Pointer(&name[0])),
	)

	if handle == 0 {
		return true // мьютекс не создался, но мы продолжаем
	}

	errCode, _, _ := procGetLastError.Call()
	if errCode == alreadyExists {
		return false // уже запущено
	}

	return true
}
