import { Component, Input }  from '@angular/core';

@Component({
  selector:    'todo-list-elements',
  templateUrl: './todo-list-elements.html',
  styleUrls:  ['./todo-list-elements.scss']
})
export class ToDoListElementsComponent {
  @Input() displayText: string = 'default text'
  @Input() flash: string = 'flash text'
  @Input() title: string = 'title text'
  @Input() body: string = 'body text'
  @Input() status: string = 'status text'
  @Input() categoryColor: string = 'categoryColor text'
  @Input() editUrl: string = '/todo/1/edit'

  constructor(){}
}
