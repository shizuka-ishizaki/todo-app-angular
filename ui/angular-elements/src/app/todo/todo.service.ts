  import { Injectable } from '@angular/core';
  import { HttpClient, HttpHeaders } from '@angular/common/http';
  import { Observable }              from 'rxjs';

  import { ToDoAdd, ToDoWithCategory }          from 'src/app/model/todo';

  /**
   * REST-API 実行時に指定する URL
   *
   * バックエンドは PlayFramework で実装し、ポート番号「9000」で待ち受けているため、
   * そのまま指定すると CORS でエラーになる
   * それを回避するため、ここではフロントエンドのポート番号「5555」を指定し、
   * Angular CLI のリバースプロキシを利用してバックエンドとの通信を実現させる
   *
   * 認証専用の URL を設定、バックエンドのapi/がフロントエンドとの連携用に設定してある
   *
   * @private
   * @memberof HttpClientService
   */
  const AUTH_API_V1: string = 'api/v1/todo'

  @Injectable({
    providedIn: 'root'
  })
  export class ToDoService {

    /**
     * コンストラクタ. HttpClientService のインスタンスを生成する
     *
     * @param {Http} Httpサービスを DI する
     * @memberof HttpClientService
     */
    constructor(private http: HttpClient) {}

    httpOptions = {
      headers: new HttpHeaders({
        'Content-Type':    'application/json',
        'withCredentials': 'true',
      })
    };

    /**
     * HTTP GET メソッドを実行する
     * （ToDoリスト取得する場合のコード）
     * @return        An `Observable` of the response body as a `any`.
     * @memberof     HttpClientService
     */
    getToDoList(): Observable<any> {
      return this.http.get<any>(`${AUTH_API_V1}/list`);
    }

    /**
     * HTTP POST メソッドを実行する
     * （ToDoリスト取得する場合のコード）
     * @return        An `Observable` of the response body as a `any`.
     * @memberof     HttpClientService
     */
    delete(payload: {id: string}): Observable<{ isSuccess: boolean }> {
      return this.http.post<{ isSuccess: boolean }>(`${AUTH_API_V1}/delete`, payload, this.httpOptions);
    }

    /**
     * HTTP GET メソッドを実行する
     * （ToDoカテゴリ一覧取得する場合のコード）
     * @return        An `Observable` of the response body as a `any`.
     * @memberof     HttpClientService
     */
     getCategorys(): Observable<any> {
      return this.http.get<any>(`${AUTH_API_V1}/category_list`);
    }

    /**
     * HTTP POST メソッドを実行する
     * （ToDoリスト取得する場合のコード）
     * @return        An `Observable` of the response body as a `any`.
     * @memberof     HttpClientService
     */
     add(todo: ToDoAdd): Observable<{ isSuccess: boolean }> {
      return this.http.post<{ isSuccess: boolean }>(`${AUTH_API_V1}/add`, todo, this.httpOptions);
    }
  }
