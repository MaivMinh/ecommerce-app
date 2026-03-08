import http from "k6/http";
import { check } from "k6";

export const options = {
  vus: 100,
  duration: "5s",
};

const token = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJOaW93SkI0Tl9FbEV4SUNvYzFCUW5wbHJaNWpVdHhURUd6YXhYQVNhakNJIn0.eyJleHAiOjE3NzMwMjkxODEsImlhdCI6MTc3Mjk5MzE4MiwiYXV0aF90aW1lIjoxNzcyOTkzMTgxLCJqdGkiOiJvbnJ0YWM6NzQxMDI1ZjUtZTliNS03ZGNiLTYyZjMtNDJmNmE3ZTZjMzUzIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo5MDkwL3JlYWxtcy9lLWNvbW1lcmNlIiwiYXVkIjoiYWNjb3VudCIsInN1YiI6Ijc5OTBhZWUxLTdkM2ItNDQ4ZC04NTcyLTVjYmFiZmMwMTMzOSIsInR5cCI6IkJlYXJlciIsImF6cCI6ImUtY29tbWVyY2UiLCJzaWQiOiIyZDNkNzAyZC1hNTcxLTQxOTctYjdlNC1jMTdhODAxNzFhM2MiLCJhY3IiOiIxIiwiYWxsb3dlZC1vcmlnaW5zIjpbIiIsImh0dHA6Ly9sb2NhbGhvc3Q6NTE3MyIsImh0dHA6Ly9sb2NhbGhvc3Q6NTE3NCJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiZGVmYXVsdC1yb2xlcy1lLWNvbW1lcmNlIiwib2ZmbGluZV9hY2Nlc3MiLCJ1bWFfYXV0aG9yaXphdGlvbiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImUtY29tbWVyY2UiOnsicm9sZXMiOlsiVVNFUiJdfSwiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJvcGVuaWQgZW1haWwgcHJvZmlsZSIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJuYW1lIjoiTWluaCBNYWkgVsSDbiIsInByZWZlcnJlZF91c2VybmFtZSI6Im1haXZhbm1pbmgiLCJnaXZlbl9uYW1lIjoiTWluaCIsImZhbWlseV9uYW1lIjoiTWFpIFbEg24iLCJlbWFpbCI6Im1haXZhbm1pbmguc2VAZ21haWwuY29tIn0.TAI_TCFyyY7-uu_sueuLpcDSGPNbyZrly_iD1xbyCGuNLZycV-rexBYQ-7TTy-Rg8CjIVePzEZMWQJ9B99MdxPXMSAhAirg8CyjRSWIqekyZ2uNffIyDxzc_vAIKpYIEdTNH67K_QZ5YcXZT_Ds_dY-HoiNE0yzoN6mZXxgirRzL0cXCTQrk6Obi0UbhHlAGUiNtepPlzjv6c1cUHZhw-qJZSLxHqa1BUhQv2y2sf6m0voI2xvMpPqccin0neIC1lFhOwUronSkXO0nYfYF0RMrSRsd_eAqJCX2WsaXikrIVDB11scOw2TtLz9FSqX5wDSMPl8IzGly0iPn5lZ6krg";

export default function () {

  const res = http.patch(
    "http://localhost:8080/api/products/atomic-update-quantity/2a6e7ec8-9b22-43df-9202-5cbfe4b0d01d?quantity=1",
    null,
    {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    }
  );

  check(res, {
    "success": (r) => r.status === 200,
    "out_of_stock": (r) => r.status !== 200
  });
}