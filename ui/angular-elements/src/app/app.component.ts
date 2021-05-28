import { Component, Injector }    from '@angular/core';
import { createCustomElement }    from '@angular/elements'
import { HelloElementsComponent } from 'src/app/sample';
import { ToDoListElementsComponent } from 'src/app/todo/todo-list-elements';
import { ToDoAddElementsComponent } from 'src/app/todo/todo-add-elements';

@Component({
  selector: 'app-root',
  template: ''
})
export class AppComponent {
  title = 'angular-elements';

  constructor(injector: Injector) {
    customElements.define('hello-elements', createCustomElement(HelloElementsComponent, {injector})),
    customElements.define('todo-list-elements', createCustomElement(ToDoListElementsComponent, {injector})),
    customElements.define('todo-add-elements', createCustomElement(ToDoAddElementsComponent, {injector}))
  }
}
