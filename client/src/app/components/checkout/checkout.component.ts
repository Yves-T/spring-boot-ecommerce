import { AsyncPipe, CurrencyPipe } from '@angular/common';
import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { Addres } from '../../common/addres';
import { Customer } from '../../common/customer';
import { Order } from '../../common/order';
import { OrderItem } from '../../common/order-item';
import { Purchase } from '../../common/purchase';
import { CartService } from '../../services/cart.service';
import { CheckoutService } from '../../services/checkout.service';
import { ShopFormService } from '../../services/shop-form.service';
import { ShopValidators } from '../../validators/shop-validators';

@Component({
  selector: 'app-checkout',
  imports: [ReactiveFormsModule, AsyncPipe, CurrencyPipe],
  templateUrl: './checkout.component.html',
  styleUrl: './checkout.component.css',
})
export class CheckoutComponent implements OnInit, OnDestroy {
  private formBuilder = inject(FormBuilder);
  private cartService = inject(CartService);
  private formService = inject(ShopFormService);
  private checkoutService = inject(CheckoutService);
  private router = inject(Router);

  totalPrice = 0;
  totalQuantity = 0;
  totalPrice$ = this.cartService.totalPrice;
  totalPriceSub: Subscription = Subscription.EMPTY;
  totalQuantity$ = this.cartService.totalQuantity;
  totalQuantitySub: Subscription = Subscription.EMPTY;
  creditCardMonths$ = this.formService.getCreditCardMoths(new Date().getMonth() + 1);
  creditCardYears$ = this.formService.getCreditCardYears();
  countries$ = this.formService.getCountries();
  billingStates$ = this.formService.getStates('');

  checkoutFormGroup = this.formBuilder.group({
    customer: this.formBuilder.group({
      firstName: new FormControl('', [
        Validators.required,
        Validators.minLength(2),
        ShopValidators.notOnlyWhiteSpace,
      ]),
      lastName: new FormControl('', [
        Validators.required,
        Validators.minLength(2),
        ShopValidators.notOnlyWhiteSpace,
      ]),
      email: new FormControl('', [
        Validators.required,
        Validators.pattern('^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$'),
      ]),
    }),
    billingAddress: this.formBuilder.group({
      street: new FormControl('', [
        Validators.required,
        Validators.minLength(2),
        ShopValidators.notOnlyWhiteSpace,
      ]),
      city: new FormControl('', [
        Validators.required,
        Validators.minLength(2),
        ShopValidators.notOnlyWhiteSpace,
      ]),
      state: new FormControl('', [Validators.required]),
      country: new FormControl('', [Validators.required]),
      zipCode: new FormControl('', [
        Validators.required,
        Validators.minLength(2),
        ShopValidators.notOnlyWhiteSpace,
      ]),
    }),
    creditCard: this.formBuilder.group({
      cardType: new FormControl('', [Validators.required]),
      nameOnCard: new FormControl('', [
        Validators.required,
        Validators.minLength(2),
        ShopValidators.notOnlyWhiteSpace,
      ]),
      cardNumber: new FormControl('', [Validators.required, Validators.pattern('[0-9]{16}')]),
      securityCode: new FormControl('', [Validators.required, Validators.pattern('[0-9]{3}')]),
      expirationMonth: [''],
      expirationYear: [''],
    }),
  });

  ngOnInit(): void {
    this.totalPriceSub = this.totalPrice$.subscribe(totalPrice => (this.totalPrice = totalPrice));
    this.totalQuantitySub = this.totalQuantity$.subscribe(
      totalQuantity => (this.totalQuantity = totalQuantity),
    );
  }

  onSubmit() {
    if (this.checkoutFormGroup.invalid) {
      this.checkoutFormGroup.markAllAsTouched();
      return;
    }

    const cartItems = this.cartService.cartItems;

    let purchase = new Purchase();

    const customerForm = this.checkoutFormGroup.controls['customer'].value;
    const customer = new Customer(
      customerForm.firstName,
      customerForm.lastName,
      customerForm.email,
    );
    purchase.customer = customer;

    let shippingAddress = this.checkoutFormGroup.controls['billingAddress'].value;
    purchase.shippingAddress = new Addres(
      shippingAddress.street || '',
      shippingAddress.city || '',
      JSON.parse(JSON.stringify(shippingAddress?.state)).name,
      JSON.parse(JSON.stringify(shippingAddress?.country)).name,
      shippingAddress.zipCode || '',
    );

    purchase.billingAddress = purchase.shippingAddress;

    const order = new Order(this.totalQuantity, this.totalPrice);
    const orderItems = cartItems.map(cartItem => new OrderItem(cartItem));
    purchase.order = order;
    purchase.orderItems = orderItems;

    this.checkoutService.placeOrder(purchase).subscribe({
      next: respone => {
        alert(
          `Your order has been received.\n Order tracking number: ${respone.orderTrackingNumber}`,
        );
        this.resetCart();
      },
      error: err => {
        alert(`There was an error: ${err.message}`);
      },
    });
  }

  resetCart() {
    this.cartService.cartItems = [];
    this.cartService.totalPrice.next(0);
    this.cartService.totalQuantity.next(0);

    this.checkoutFormGroup.reset();

    this.router.navigateByUrl('/products');
  }

  handleMonthsAndYears() {
    const creditCardFormGroup = this.checkoutFormGroup.get('creditCard');
    const curentYear = new Date().getFullYear();
    const selectedYear = Number(creditCardFormGroup?.value.expirationYear);

    let startMonth;
    if (curentYear === selectedYear) {
      startMonth = new Date().getMonth() + 1;
    } else {
      startMonth = 1;
    }

    this.creditCardMonths$ = this.formService.getCreditCardMoths(startMonth);
  }

  getStates(formGroupName: string) {
    const formGroup = this.checkoutFormGroup.get(formGroupName);
    const countryCode = formGroup?.value.country.code;
    this.billingStates$ = this.formService.getStates(countryCode);
  }

  get firstName() {
    return this.checkoutFormGroup.get('customer.firstName');
  }

  get lastName() {
    return this.checkoutFormGroup.get('customer.lastName');
  }

  get email() {
    return this.checkoutFormGroup.get('customer.email');
  }

  get addressStreet() {
    return this.checkoutFormGroup.get('billingAddress.street');
  }

  get addressCity() {
    return this.checkoutFormGroup.get('billingAddress.city');
  }

  get addressState() {
    return this.checkoutFormGroup.get('billingAddress.state');
  }

  get addressZip() {
    return this.checkoutFormGroup.get('billingAddress.zipCode');
  }

  get addressCountry() {
    return this.checkoutFormGroup.get('billingAddress.country');
  }

  get creditCardType() {
    return this.checkoutFormGroup.get('creditCard.cardType');
  }

  get creditCardNameOnCard() {
    return this.checkoutFormGroup.get('creditCard.nameOnCard');
  }

  get creditCardNumber() {
    return this.checkoutFormGroup.get('creditCard.cardNumber');
  }

  get creditCardSecurityCode() {
    return this.checkoutFormGroup.get('creditCard.securityCode');
  }

  ngOnDestroy(): void {
    if (this.totalPriceSub) {
      this.totalPriceSub.unsubscribe();
    }
    if (this.totalQuantitySub) {
      this.totalQuantitySub.unsubscribe();
    }
  }
}
