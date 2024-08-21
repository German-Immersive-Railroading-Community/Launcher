{
  description = "A very basic flake";

  inputs.nixpkgs.url = "github:nixos/nixpkgs/nixpkgs-unstable";

  outputs = { self, nixpkgs }: {

    devShells.x86_64-linux.default = let
        pkgs = import nixpkgs { system = "x86_64-linux"; };
        jdk = pkgs.jdk21;
    in pkgs.mkShell {
        buildInputs = with pkgs; [
          git
        
          jdk
          graalvm-ce          
          (callPackage gradle-packages.gradle_8 {
            java = jdk;
          })
        ];
    };
  };
}