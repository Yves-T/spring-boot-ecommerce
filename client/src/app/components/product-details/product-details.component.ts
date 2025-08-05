import { AsyncPipe, CurrencyPipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { map, switchMap } from 'rxjs';
import { CartItem } from '../../common/cart-item';
import { Product } from '../../common/product';
import { CartService } from '../../services/cart.service';
import { ProductService } from '../../services/product.service';

@Component({
  selector: 'app-product-details',
  imports: [AsyncPipe, CurrencyPipe, RouterModule],
  templateUrl: './product-details.component.html',
  styleUrl: './product-details.component.css',
})
export class ProductDetailsComponent {
  product!: Product;
  productService = inject(ProductService);
  cartService = inject(CartService);
  route = inject(ActivatedRoute);
  product$ = this.route.paramMap.pipe(
    map(params => params.get('id') || '1'),
    switchMap((id: string) => this.productService.getProduct(parseInt(id))),
  );

  addToCart(product: Product) {
    console.log(`Adding to cart: ${product.name}, ${product.unitPrice}`);
    const cartItem = new CartItem(product);
    this.cartService.addToCart(cartItem);
  }
}
