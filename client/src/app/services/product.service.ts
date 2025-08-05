import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { map, Observable, of } from 'rxjs';
import { Product } from '../common/product';
import { ProductCategory } from '../common/product-category';

export interface GetResponseProducts {
  _embedded: {
    products: Product[];
  };
  page: {
    size: number;
    totalElements: number;
    totalPages: number;
    number: number;
  };
}

interface GetResponseCategory {
  _embedded: {
    productCategory: ProductCategory[];
  };
}

@Injectable({
  providedIn: 'root',
})
export class ProductService {
  private http = inject(HttpClient);
  private baseUrl = 'http://localhost:8080/api/';
  private productUrl = this.baseUrl + 'products';
  private categoryUrl = this.baseUrl + 'product-category';
  private searchUrl = this.baseUrl + 'products/search';

  getProduct(productId: number): Observable<Product> {
    const productUrl = `${this.productUrl}/${productId}`;

    return this.http.get<Product>(productUrl);
  }

  getProductListPaginate(
    page: number,
    pageSize: number,
    categoryId: number,
  ): Observable<GetResponseProducts> {
    const searchUrl =
      `${this.searchUrl}/findByCategoryId?id=${categoryId}` + `&page=${page}&size=${pageSize}`;

    return this.http.get<GetResponseProducts>(searchUrl);
  }

  searchProductsPaginate(
    page: number,
    pageSize: number,
    keyword: string,
  ): Observable<GetResponseProducts> {
    const searchUrl =
      `${this.searchUrl}/findByNameContaining?name=${keyword}` + `&page=${page}&size=${pageSize}`;

    return this.http.get<GetResponseProducts>(searchUrl);
  }

  getProductList(categoryId: number): Observable<Product[]> {
    const searchUrl = `${this.searchUrl}/findByCategoryId?id=${categoryId}`;
    return this.getProducts(searchUrl);
  }

  searchProducts(keyword: string | null): Observable<GetResponseProducts> {
    if (!keyword) {
      return of({
        _embedded: { products: [] },
        page: {
          size: 0,
          totalElements: 0,
          totalPages: 0,
          number: 0,
        },
      });
    }
    const searchUrl = `${this.searchUrl}/findByNameContaining?name=${keyword}`;
    return this.getProducts(searchUrl).pipe(
      map(data => {
        const response: GetResponseProducts = {
          _embedded: { products: data },
          page: {
            size: 0,
            totalElements: 0,
            totalPages: 0,
            number: 0,
          },
        };
        return response;
      }),
    );
  }

  private getProducts(searchUrl: string): Observable<Product[]> {
    return this.http
      .get<GetResponseProducts>(searchUrl)
      .pipe(map(response => response._embedded.products));
  }

  getProductCategories(): Observable<ProductCategory[]> {
    return this.http
      .get<GetResponseCategory>(this.categoryUrl)
      .pipe(map(response => response._embedded.productCategory));
  }
}
