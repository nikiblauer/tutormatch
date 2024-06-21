export interface ImportDto {
  importId: String;
}

export interface ImportStatusDto extends ImportDto {
  status: String;
  progress: Number;
  importDate: Date;
}
