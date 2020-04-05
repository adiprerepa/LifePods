package main

import (
	"context"
	"flag"
	"fmt"
	"github.com/golang/glog"
	"github.com/grpc-ecosystem/grpc-gateway/runtime"
	"google.golang.org/grpc"
	"net/http"
)
import gw "lifepods_gateway"

var (
	grpcServerEndpoint = flag.String("lifepod-server-endpoint", "localhost:2001", "LifePod gRPC service implementation")
)

func run() []error {
	ctx := context.Background()
	ctx, cancel := context.WithCancel(ctx)
	defer cancel()

	// Register LifePods gRPC service
	mux := runtime.NewServeMux()
	opts := []grpc.DialOption{grpc.WithInsecure()}
	err_1 := gw.RegisterAuthenticationServiceHandlerFromEndpoint(ctx, mux, *grpcServerEndpoint, opts)
	err_2 := gw.RegisterEventServiceHandlerFromEndpoint(ctx, mux, *grpcServerEndpoint, opts)
	if err_1 != nil || err_2 != nil {
		return []error {err_2, err_1}
	}
	return []error {http.ListenAndServe(":8081", mux)}
}

func main() {
	fmt.Println("Attempting to start LifePod grpc-gateway proxy")
	defer glog.Flush()
	err := run();
	if err != nil {
		for _, e := range err {
			glog.Fatal(e)
		}
	}
}