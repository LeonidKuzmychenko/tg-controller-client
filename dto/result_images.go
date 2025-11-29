package dto

type ResultImages struct {
	Result
	Images [][]byte `json:"images"`
}

func (ResultImages) IsResult() {}
