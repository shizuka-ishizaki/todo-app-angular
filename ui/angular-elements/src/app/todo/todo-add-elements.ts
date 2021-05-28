import { Component, OnInit }  from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { ToDoService } from 'src/app/todo/todo.service';
import { ToDoAdd } from 'src/app/model/todo';

@Component({
  selector:    'todo-add-elements',
  templateUrl: './todo-add-elements.html',
  styleUrls:  ['./todo-add-elements.scss']
})
export class ToDoAddElementsComponent implements OnInit {
  categorys: any;
  flash: string = ''
  error: string = ''

  constructor(
    private toDoService: ToDoService
  ){}

  ngOnInit() {
    this.getCategorys();
  }

  getCategorys(): void {
    this.toDoService.getCategorys()
      .subscribe(categorys => {
        this.categorys = categorys;
      });
  }

  onSubmit() {
    this.addTodo(this.todoForm.value)
  }

  addTodo(todo: ToDoAdd) {
    this.toDoService.add(todo)
      .subscribe(isResult => {
        if (isResult.isSuccess) {
          this.error = ""
          this.flash = "ToDoを登録しました!"
        } else {
          this.flash = ""
          this.error = "ToDoの登録に失敗しました。。。"
        }
      });
  }

  title = new FormControl('',
    [
      Validators.required,
    ],
  );

  body = new FormControl('',
    [
      Validators.required,
    ],
  );

  category = new FormControl('',
    [
      Validators.required,
    ],
  );

  todoForm = new FormGroup({
    title:    this.title,
    body:     this.body,
    category: this.category,
  });
}
