import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
// HTTP クライアントのための import ( Angular 5.0.0 以降はこちらを使う )
import { HttpClientModule } from '@angular/common/http';

import { AppComponent } from './app.component';
import { HelloElementsComponent } from 'src/app/sample';
import { ToDoListElementsComponent } from 'src/app/todo/todo-list-elements';
import { ToDoAddElementsComponent } from 'src/app/todo/todo-add-elements';

@NgModule({
  declarations: [
    AppComponent,
    HelloElementsComponent,
    ToDoListElementsComponent,
    ToDoAddElementsComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule
  ],
  providers: [],
  bootstrap: [AppComponent],
  entryComponents: [
    ToDoListElementsComponent,
    ToDoAddElementsComponent
  ]
})
export class AppModule { }
