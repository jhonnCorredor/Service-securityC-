﻿using Entity.Model.Operational;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Entity.Dto.Parameter
{
    public class FarmCropDto
    {
        public int Id { get; set; }
        public int CropId { get; set; }
        public int FarmId { get; set; }
        public int Num_hectareas { get; set; }
        public bool State { get; set; }
    }
}
