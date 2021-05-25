import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppComponent } from './app.component';
import { HelloElementsComponent } from 'src/app/sample';
import { ToDoListElementsComponent } from 'src/app/todo';

@NgModule({
  declarations: [
    AppComponent,
    HelloElementsComponent,
    ToDoListElementsComponent
  ],
  imports: [
    BrowserModule
  ],
  providers: [],
  bootstrap: [AppComponent],
  entryComponents: [
    HelloElementsComponent,
    ToDoListElementsComponent
  ]
})
export class AppModule { }
