export interface ToDoWithCategory {
  id:             string;
  categoryId:     string;
  title:          string;
  body:           string;
  status:         string;
  categoryName:   string;
  categoryColor:  string;
  editUrl:        string;
}

export interface DeleteId {
  id:             string;
}

export interface ApiResult {
  isSuccess: boolean
}

export interface ToDoAdd {
  title:    string;
  body:     string;
  category: string;
}
