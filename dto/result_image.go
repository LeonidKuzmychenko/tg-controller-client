package dto

type ResultImage struct {
	Result
	Image []byte `json:"image"`
}

func (ResultImage) IsResult() {}
