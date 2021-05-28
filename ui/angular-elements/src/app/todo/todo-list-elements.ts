import { Component, OnInit }  from '@angular/core';
import { ToDoService } from 'src/app/todo/todo.service';

@Component({
  selector:    'todo-list-elements',
  templateUrl: './todo-list-elements.html',
  styleUrls:  ['./todo-list-elements.scss']
})
export class ToDoListElementsComponent implements OnInit {
  flash: string = ''
  error: string = ''
  todos: any;

  constructor(
    private toDoService: ToDoService
  ){}

  ngOnInit() {
    this.getList()
  }

  getList(): void {
    this.toDoService.getToDoList()
      .subscribe(todos => {
        this.todos = todos;
      });
  }



  onClickDelete(todoId: string): void  {
    this.flash = ""
    this.error = ""
    var result = window.confirm("本当に削除してもいいですか？");
    if( result ) {
      this.deleteToDo(todoId);
    }　else {
       window.alert('キャンセルされました');
    }
  }

  deleteToDo(todoId: string): void {
    this.toDoService.delete({id: todoId})
      .subscribe(isResult => {
        if (isResult.isSuccess) {
          this.getList()
          this.error = ""
          this.flash = "ToDoを削除しました!"
        } else {
          this.flash = ""
          this.error = "ToDoの削除に失敗しました。。。"
        }
      });
  }
}
